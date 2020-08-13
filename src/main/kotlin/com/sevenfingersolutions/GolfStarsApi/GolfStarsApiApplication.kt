package com.sevenfingersolutions.GolfStarsApi


import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.*
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import java.io.Serializable
import java.util.*
import javax.persistence.*


@SpringBootApplication
class GolfStarsApiApplication {

	@Bean
	fun simpleCorsFilter(): FilterRegistrationBean<CorsFilter> {
		val source = UrlBasedCorsConfigurationSource()
		val config = CorsConfiguration()
		config.allowCredentials = true
		config.allowedOrigins = listOf("http://localhost:4200")
		config.allowedMethods = listOf("*");
		config.allowedHeaders = listOf("*")
		source.registerCorsConfiguration("/**", config)
		val bean = FilterRegistrationBean(CorsFilter(source))
		bean.order = Ordered.HIGHEST_PRECEDENCE
		return bean
	}
}

@Configuration
class RestConfiguration : RepositoryRestConfigurer {
	override fun configureRepositoryRestConfiguration(config: RepositoryRestConfiguration?) {
		config?.exposeIdsFor(PlayerGroup::class.java)
		config?.exposeIdsFor(GolfUser::class.java)
		config?.exposeIdsFor(UserGroups::class.java)
		config?.exposeIdsFor(TournamentEnrollment::class.java)
		config?.exposeIdsFor(Tournament::class.java)
		config?.exposeIdsFor(TournamentGolfer::class.java)
		config?.setBasePath("/api")
	}
}

fun main(args: Array<String>) {
	runApplication<GolfStarsApiApplication>(*args)
}
/*
PlayerGroup is data entity that maps to the MySql database. It has a unique id, a name and a number of members
 */

@Entity
data class PlayerGroup(@Id @GeneratedValue(strategy = GenerationType.IDENTITY)  var playerGroupId: Int? = null ,
					   var groupName: String? = null,
					   var totalNumberOfPlayers: Int? = null,
					   var hasAllMembers: Boolean = false,
					   var groupOwner: String? = null)


@RepositoryRestResource
interface GroupsRepository : JpaRepository<PlayerGroup, Int> {
	fun findByPlayerGroupId(playerGroupId: Int?): PlayerGroup
}



@Component
@RepositoryEventHandler(PlayerGroup::class)
class AddUserToGroup(val groupsRepository: GroupsRepository, val usersRepository: UsersRepository, val userGroupsServices: UserGroupsServices) {
	@HandleAfterCreate
	fun handleCreate(group: PlayerGroup) {
		val userName: String =  SecurityContextHolder.getContext().getAuthentication().name
		println("Created group: $group with user: $userName")
		group.groupOwner = userName
		groupsRepository.save(group)

		val user : GolfUser = usersRepository.findByUserName(userName);
		val userId : Int? = user.golfUserId
		val groupId : Int? = group.playerGroupId
		println("userID: $userId")
		println("groupId: $groupId")
		val userGroupId = userId?.let { groupId?.let { it1 -> UserGroupsId(it, it1) } }
		println("userGroupID: $userGroupId")
		val userGroup = UserGroups(userGroupId)
		userGroupsServices.save(userGroup)


	}

	@HandleAfterSave
	fun handleSave(group: PlayerGroup){
		val playerGroupId: Int? = group.playerGroupId
		val playerGroupOwner : String? = group.groupOwner
		println("Updating UserGroups after saving change to a PlayerGroup")

		playerGroupId?.let { userGroupsServices.findByUserGroupsId(it) }?.forEach {
			it.groupName = group.groupName
			it.isOwner = it.golfUserName.equals(playerGroupOwner)
			userGroupsServices.save(it)
		}

	}

}

@Component
@RepositoryEventHandler(PlayerGroup::class)
class DeleteGroup( val chatMessageRepository: ChatMessageRepository, val userGroupsServices: UserGroupsServices) {

	@HandleBeforeDelete
	fun handleDelete(group: PlayerGroup) {
		val groupId = group.playerGroupId
		val userGroupsToDelete : List<UserGroups> = groupId?.let { userGroupsServices.findByUserGroupsId(it) }!!
		userGroupsServices.deleteListOfUserGroups(userGroupsToDelete)

		val chatMessagesToDelete : List<ChatMessage> = groupId.let { chatMessageRepository.findAllByGroupId(it)}
		chatMessageRepository.deleteInBatch(chatMessagesToDelete)
	}
}

/*
GolfUser is the data class a unique player id and username. It is used to link them to their groups and to messages.
It is not needed for sign in, OAuth2 authenication is handled by the okta plugin. The username for okta and for this
class ought to be the same
 */

@Entity
data class GolfUser(@Id @GeneratedValue(strategy = GenerationType.IDENTITY)  var golfUserId: Int? = null ,
					var userName: String? = null, @JsonIgnore var userOktaId: String? = null)

@RepositoryRestResource
interface UsersRepository : JpaRepository<GolfUser, Int> {
	fun findByUserName(userName: String?): GolfUser
	fun findByGolfUserId(id : Int?) : GolfUser
}

@Component
@RepositoryEventHandler(GolfUser::class)
class AddOktaUserNameToGolfUser {

	@HandleBeforeCreate
	fun handleCreate(golfUser: GolfUser) {
		val oktaUsername: String =  SecurityContextHolder.getContext().getAuthentication().name
		println("Creating record: $golfUser with user: $oktaUsername")
		golfUser.userOktaId = oktaUsername
	}
}
/**
 * UserGroups is a table in the database that contains userIDs and groupIds, allowing a query that will return all the
 * members of a group. It will also allow logic be applied to enforce the numbers in a group.
 * The MySql table uses a composite primary key comprised of the two foreign keys. To facilitate this relationship
 * it was necessary to create two classes, the UserGroups class and the separate UserGroupsId class that
 * provides the composite ID. This took many hours to figure out!!!
 */



@Entity
data class UserGroups  (@EmbeddedId @JsonIgnore  var userGroupsId: UserGroupsId? = null, var groupName : String? = null, var golfUserName : String? = null, var isOwner : Boolean = false,  @GeneratedValue(strategy = GenerationType.AUTO)  var userGroupsExportId: Int? = null) {
	override fun toString(): String {
		val mapper = ObjectMapper()
		mapper.enable(SerializationFeature.INDENT_OUTPUT)
		val jsonString = mapper.writeValueAsString(this)
		return jsonString
	}
}

	@Embeddable
class UserGroupsId (
		@Column(name = "golf_user_id_enrolled")
		var golfUserIdEnrolled: Int = -1,

		@Column(name = "player_group_id_enrolled")
		var playerGroupIdEnrolled: Int = -1

):Serializable {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || javaClass != other.javaClass) return false
		val that: UserGroupsId = other as UserGroupsId
		return Objects.equals(golfUserIdEnrolled, that.golfUserIdEnrolled) &&
				Objects.equals(playerGroupIdEnrolled, that.playerGroupIdEnrolled)
	}

	override fun hashCode(): Int {
		return Objects.hash(golfUserIdEnrolled, playerGroupIdEnrolled)
	}
}

@RepositoryRestResource
interface UserGroupsRepository : JpaRepository<UserGroups, UserGroupsId>{
	fun findByUserGroupsId(userGroupsId: UserGroupsId?): UserGroups
}
/*
@Component
@RepositoryEventHandler(UserGroups::class)
class AddUserGroupForExport(val userGroupsForExportRepository: UserGroupsForExportRepository) {
	@HandleAfterCreate
	fun handleCreate(userGroups: UserGroups) {
		val userGroupsForExport: UserGroupsForExport = UserGroupsForExport(exportId = userGroups.userGroupsExportId, groupName = userGroups.groupName, golfUserName = userGroups.golfUserName, isOwner = userGroups.isOwner)
		userGroupsForExportRepository.save(userGroupsForExport)
	}
}
*/
/*
@Entity
data class UserGroupsForExport (@Id @GeneratedValue(strategy = GenerationType.IDENTITY)  var userGroupForExportId: Int? = null , var exportId : Int? = null, var groupName: String?, var golfUserName: String?, var isOwner: Boolean)

@RepositoryRestResource
interface UserGroupsForExportRepository : JpaRepository<UserGroupsForExport, Int>

/*
Tournament enrollment is class similar in function to the above class UserGroups.
It takes a composite primary key from the id of tournament golfers and the id of tournaments.
It is therefore possible to track into which tournaments golfers are entered and how they perfrom.
 */
*/
@Entity
data class TournamentEnrollment  (@EmbeddedId var TournamentEnrollmentId: TournamentEnrollmentId? = null, var oddsToOne : Int? = null, var tournamentPosition : Int? = null, var pickedBy : Int? = null)

@Embeddable
class TournamentEnrollmentId (
		@Column(name = "tournament_golfer_id_enrolled")
		var tournamentGolferIdEnrolled: Int = -1,

		@Column(name = "tournament_id_enrolled")
		var tournamentIdEnrolled: Int = -1

):Serializable

@RepositoryRestResource
interface TournamentEnrollmentRepository : JpaRepository<TournamentEnrollment, TournamentEnrollmentId>


@Entity
data class TournamentGroup  (@EmbeddedId var TournamentGroupId: TournamentGroupId? = null)

@Embeddable
class TournamentGroupId (
		@Column(name = "tournament_linked_id")
		var tournamentLinkedId: Int = -1,

		@Column(name = "group_linked_id")
		var groupLinkedId: Int = -1

):Serializable

@RepositoryRestResource
interface TournamentGroupRepository : JpaRepository<TournamentGroup, TournamentGroupId>

/*
A class to store message data shared between users.
*/
@Entity
data class ChatMessage(@Id @GeneratedValue(strategy = GenerationType.IDENTITY)  var messageId: Int? = null ,
					var playerUserName: String? = null, var groupId : Int? = null, var messageBody: String? = null,
					   @JsonIgnore  var messageDate : Long? =  System.currentTimeMillis())


@Component
@RepositoryEventHandler(ChatMessage::class)
class AddTimeToMessage {

	@HandleBeforeCreate
	fun handleCreate(message: ChatMessage) {
		val date: Long =  System.currentTimeMillis()
		println("Creating time stamp: $message with date: $date")
		message.messageDate = date
	}
}

@RepositoryRestResource
interface ChatMessageRepository : JpaRepository<ChatMessage, Int>{
	fun findAllByGroupId(groupId: Int) : List<ChatMessage>
}

@Entity
data class Tournament(@Id @GeneratedValue(strategy = GenerationType.IDENTITY)  var tournamentId: Int? = null ,
							var tournamentName: String? = null, var tournamentLocation: String? = null, var tournamentStartDate : String? = null, var tournamentEndDate : String? = null)

@RepositoryRestResource
interface TournamentRepository : JpaRepository<Tournament, Int>


@Entity
data class TournamentGolfer(@Id @GeneratedValue(strategy = GenerationType.IDENTITY)  var tournamentGolferId: Int? = null ,
					var firstName: String? = null, var secondName: String? = null, var imageUrl: String? = null, var nationality : String? = null)

@RepositoryRestResource
interface TournamentGolferRepository : JpaRepository<TournamentGolfer, Int>


