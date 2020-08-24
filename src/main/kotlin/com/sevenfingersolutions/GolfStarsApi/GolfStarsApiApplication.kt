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


/*
This function allow both the api and the client to be run from the same machine
without causing an issue.
 */


	@Bean
	fun simpleCorsFilter(): FilterRegistrationBean<CorsFilter> {
		val source = UrlBasedCorsConfigurationSource()
		val config = CorsConfiguration()
		config.allowCredentials = true
		config.allowedOrigins = listOf("http://localhost:4200")
		config.allowedMethods = listOf("*")
		config.allowedHeaders = listOf("*")
		source.registerCorsConfiguration("/**", config)
		val bean = FilterRegistrationBean(CorsFilter(source))
		bean.order = Ordered.HIGHEST_PRECEDENCE
		return bean
	}
}

/*
The following function allows IDs to be exposed to the client.
This was necessary to solve a problem where, when editing an entity on the client side,
if it couldn't see the ID, it would try to save a new instance of the data class, rather than
updating the existing one.
 */

@Configuration
class RestConfiguration : RepositoryRestConfigurer {
	override fun configureRepositoryRestConfiguration(config: RepositoryRestConfiguration?) {
		config?.exposeIdsFor(PlayerGroup::class.java)
		config?.exposeIdsFor(GolfUser::class.java)
		config?.exposeIdsFor(UserGroups::class.java)
		config?.exposeIdsFor(TournamentEnrollment::class.java)
		config?.exposeIdsFor(Tournament::class.java)
		config?.exposeIdsFor(TournamentGolfer::class.java)
		config?.exposeIdsFor(TournamentGroup::class.java)
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
					   var groupOwner: String? = null,
						var isPrivate : Boolean = false)


@RepositoryRestResource
interface GroupsRepository : JpaRepository<PlayerGroup, Int> {
	fun findByPlayerGroupId(playerGroupId: Int?): PlayerGroup
	fun findAllByGroupOwnerAndGroupNameContainingIgnoringCase(name: String, term : String):List<PlayerGroup>
}


/*
The creation of a new Group by a user necessitates the creation of both a new UserGroups, to link the user to the group,
and a new tournamentGroup, to link this new group to a list of Tournament Golfers and player selections.
After a new group is created, the person creating is automatically linked as a member of that group.
 */

@Component
@RepositoryEventHandler(PlayerGroup::class)
class AddUserToGroup(val groupsRepository: GroupsRepository, val usersRepository: UsersRepository,
					 val userGroupsServices: UserGroupsServices, val tournamentRepository: TournamentRepository,
						val tournamentEnrollmentRepository: TournamentEnrollmentRepository, val tournamentGroupRepository: TournamentGroupRepository) {
	@HandleAfterCreate
	fun handleCreate(group: PlayerGroup) {
		val CURRENT_TOURNAMENT =1

		val userName: String =  SecurityContextHolder.getContext().getAuthentication().name
		println("Created group: $group with user: $userName")

		// The following code creates a UserGroups entity each time a new PlayerGroup is created
		var user : GolfUser? = usersRepository.findByUserName(userName)
		if(user==null) {
			user = GolfUser(userName = userName)
			usersRepository.save(user)
		}
		group.groupOwner = userName
		groupsRepository.save(group)
		val userId : Int? = user.golfUserId
		val groupId : Int? = group.playerGroupId
		val userGroupId = userId?.let { groupId?.let { it1 -> UserGroupsId(it, it1) } }
		val userGroup = UserGroups(userGroupsId = userGroupId)
		userGroupsServices.save(userGroup)

		// The following code creates a TournamentGroups entity each time a new PlayerGroup is created

		val tournamentEnrollments: List<TournamentEnrollment> = tournamentEnrollmentRepository.findAllByTournamentIdSearch(CURRENT_TOURNAMENT)
		tournamentEnrollments.forEach {
			val tournamentGroupId = group.playerGroupId?.let { it1 -> it.tournamentEnrollmentPrimaryKey?.let { it2 -> TournamentGroupId(it2, it1) } }
			val tournamentGroup = TournamentGroup(tournamentGroupId = tournamentGroupId, playerGroupFilter = group.playerGroupId, golferId = it.golferId, golferFirstName = it.golferFirstName,
					golferSecondName = it.golferSecondName, golferImageUrl = it.golferImageUrl, golferNationality = it.golferNationality, oddsToOne = it.oddsToOne, tournamentPosition = it.tournamentPosition,
					finalScore = it.finalScore)
			tournamentGroupRepository.save(tournamentGroup)
		}

	}
/*
If the PlayerGroup is edited, the following function automatically
update the related UserGroup after the PlayerGroup is saved.
 */
	@HandleAfterSave
	fun handleSave(group: PlayerGroup){
		val playerGroupId: Int? = group.playerGroupId
		val playerGroupOwner : String? = group.groupOwner
		playerGroupId?.let { userGroupsServices.findByPlayerGroupsId(it) }?.forEach {
			it.groupName = group.groupName
			it.isOwner = it.golfUserName.equals(playerGroupOwner)
			userGroupsServices.save(it)
		}

	}
}
/*
If the PlayerGroup is deleted, the following function automatically
deletes the related UserGroups.
 */
@Component
@RepositoryEventHandler(PlayerGroup::class)
class DeleteGroup( val chatMessageRepository: ChatMessageRepository, val userGroupsServices: UserGroupsServices, val tournamentGroupServices: TournamentGroupServices) {

	@HandleBeforeDelete
	fun handleDelete(group: PlayerGroup) {
		val groupId = group.playerGroupId
		val userGroupsToDelete : List<UserGroups> = groupId?.let { userGroupsServices.findByPlayerGroupsId(it) }!!
		userGroupsServices.deleteListOfUserGroups(userGroupsToDelete)
		val tournamentGroupsToDelete: List<TournamentGroup> = groupId.let { tournamentGroupServices.findAllByPlayerGroupId(it)}
		tournamentGroupServices.deleteListOfTournamentGroups(tournamentGroupsToDelete);
		val chatMessagesToDelete : List<ChatMessage> = groupId.let { chatMessageRepository.findAllByGroupId(it)}
		chatMessageRepository.deleteInBatch(chatMessagesToDelete)
	}
}

/*
GolfUser is the data class a unique player id and username. It is used to link them to their groups.
It is not needed for sign in, OAuth2 authentication is handled by the okta plugin. The username for okta and for this
are the same
 */

@Entity
data class GolfUser(@Id @GeneratedValue(strategy = GenerationType.IDENTITY)  var golfUserId: Int? = null ,
					var userName: String? = null, var firstName: String? = null, var familyName: String? = null)

@RepositoryRestResource
interface UsersRepository : JpaRepository<GolfUser, Int> {
	fun findByUserName(userName: String?): GolfUser?
	fun findByGolfUserId(id : Int?) : GolfUser
}

/**
 * UserGroups is a table in the database that contains userIDs and groupIds, allowing a query that will return all the
 * members of a group. It will also allow logic be applied to enforce the numbers in a group.
 * The MySql table uses a unique composite  key comprised of the two foreign keys. To facilitate this relationship
 * it was necessary to create two classes, the UserGroups class and the separate UserGroupsId class that
 * provides the composite ID.
 */


@Entity
data class UserGroups  (@Id @GeneratedValue(strategy = GenerationType.IDENTITY)  var userGroupsPrimaryKey : Int? = null,
						@Embedded   var userGroupsId: UserGroupsId? = null, var groupName : String? = null,
						var golfUserName : String? = null, var isOwner : Boolean = false) {
	override fun toString(): String {
		val mapper = ObjectMapper()
		mapper.enable(SerializationFeature.INDENT_OUTPUT)
		return mapper.writeValueAsString(this)
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
interface UserGroupsRepository : JpaRepository<UserGroups, Int>{
	fun findUserGroupsByUserGroupsId(userGroupsId: UserGroupsId?): UserGroups?
	fun findUserGroupsByGolfUserName(userName: String?):List<UserGroups?>
}


@Component
@RepositoryEventHandler(UserGroups::class)
class AddDetailsToUserGroups(val groupsRepository: GroupsRepository, val usersRepository: UsersRepository) {

	@HandleBeforeCreate
	fun handleCreate( userGroups: UserGroups) {
		println("handle before create: $userGroups")
		userGroups.isOwner = false
		var golfUser: GolfUser? = usersRepository.findByUserName(userGroups.golfUserName)
			if(golfUser==null) {
				golfUser = GolfUser(userName = userGroups.golfUserName)
				usersRepository.save(golfUser)
			}
		userGroups.userGroupsId?.golfUserIdEnrolled  = golfUser.golfUserId!!
		val playerGroupId = userGroups.userGroupsId?.playerGroupIdEnrolled

		val playerGroup: PlayerGroup? = playerGroupId?.let { groupsRepository.findByPlayerGroupId(it) }
		if (playerGroup != null) {
			userGroups.groupName = playerGroup.groupName
		}

		println("Creating record: $userGroups")

	}
}


/*
Tournament enrollment is class similar in function to the above class UserGroups.
It takes a composite primary key from the id of tournament golfers and the id of tournaments.
It is therefore possible to track into which tournaments golfers are entered and how they perform.
 */

@Entity
data class TournamentEnrollment  (@Id @GeneratedValue(strategy = GenerationType.IDENTITY)  var tournamentEnrollmentPrimaryKey : Int? = null,
								   @Embedded var tournamentEnrollmentId: TournamentEnrollmentId? = null,
								  var tournamentIdSearch: Int? = null,
								  var golferId :Int? = null, var golferFirstName: String? = null,
								  var golferSecondName: String? = null,
								  var golferImageUrl: String? = null,
								  var golferNationality: String? = null,
								  var oddsToOne : Double? = null,
								  var tournamentPosition : Int? = null, var finalScore : Int? = null)

@Embeddable
class TournamentEnrollmentId (
		@Column(name = "tournament_golfer_id_enrolled")
		var tournamentGolferIdEnrolled: Int = -1,

		@Column(name = "tournament_id_enrolled")
		var tournamentIdEnrolled: Int = -1

):Serializable {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || javaClass != other.javaClass) return false
		val that: TournamentEnrollmentId = other as TournamentEnrollmentId
		return Objects.equals(tournamentGolferIdEnrolled, that.tournamentGolferIdEnrolled) &&
				Objects.equals(tournamentIdEnrolled, that.tournamentIdEnrolled)
	}

	override fun hashCode(): Int {
		return Objects.hash(tournamentGolferIdEnrolled, tournamentIdEnrolled)
	}
}

@RepositoryRestResource
interface TournamentEnrollmentRepository : JpaRepository<TournamentEnrollment, Int>{
	fun findAllByTournamentIdSearch(tournamentIdSearch: Int?): List<TournamentEnrollment>
}

/*
The TournamentGroup data class links all the players enrolled in a tournament to a PlayerGroup.
This allows the same golfers to be represented separately in multiple PlayerGroups
 */


@Entity
data class TournamentGroup  (@Id @GeneratedValue(strategy = GenerationType.IDENTITY)  var tournamentGroupPrimaryKey : Int? = null,
							 @Embedded var tournamentGroupId: TournamentGroupId? = null,
							 var playerGroupFilter: Int? = null,
							 var golferId :Int? = null, var golferFirstName: String? = null,
							 var golferSecondName: String? = null,
							 var golferImageUrl: String? = null,
							 var golferNationality: String? = null,
							 var oddsToOne : Double? = null,
							 var tournamentPosition : Int? = null, var finalScore : Int? = null,
							 var pickedById : Int? = null, var pickedByUsername : String? = null,
							 var isPicked: Boolean = false)

@Embeddable
class TournamentGroupId (
		@Column(name = "tournament_enrollment_foreign_id")
		var tournamentEnrollmentForeignId: Int = -1,

		@Column(name = "player_group_foreign_id")
		var playerGroupForeignId: Int = -1

):Serializable {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || javaClass != other.javaClass) return false
		val that: TournamentGroupId = other as TournamentGroupId
		return Objects.equals(tournamentEnrollmentForeignId, that.tournamentEnrollmentForeignId) &&
				Objects.equals(playerGroupForeignId, that.playerGroupForeignId)
	}

	override fun hashCode(): Int {
		return Objects.hash(tournamentEnrollmentForeignId, playerGroupForeignId)
	}
}

@RepositoryRestResource
interface TournamentGroupRepository : JpaRepository<TournamentGroup, Int> {
	fun findAllByPlayerGroupFilter(playerGroupId: Int?): List<TournamentGroup>
}

@Component
@RepositoryEventHandler(TournamentGroup::class)
class SaveGroup() {
	@HandleBeforeSave
	fun handleSave(tournamentGroup: TournamentGroup) {

		println("Saving this tournament group: $tournamentGroup")
		if(tournamentGroup.isPicked == true ) {
			val userName: String =  SecurityContextHolder.getContext().getAuthentication().name
			tournamentGroup.pickedByUsername = userName;
		} else {
			tournamentGroup.pickedByUsername = null
		}
	}
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

/*
The chat message entity was created below to allow the implementation of a chat function at a later date
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
