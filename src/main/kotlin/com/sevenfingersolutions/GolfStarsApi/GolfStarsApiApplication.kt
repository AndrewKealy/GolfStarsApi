package com.sevenfingersolutions.GolfStarsApi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

import com.fasterxml.jackson.annotation.JsonIgnore


import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.io.Serializable
import java.sql.Date
import javax.persistence.*

@SpringBootApplication
class GolfStarsApiApplication

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
					   @JsonIgnore var groupOwner: String? = null)

@RepositoryRestResource
interface GroupsRepository : JpaRepository<PlayerGroup, Long> {
	fun findByPlayerGroupId(playerGroupId: Int?): PlayerGroup
}

@Component
@RepositoryEventHandler(PlayerGroup::class)
class AddUserToGroup {

	@HandleBeforeCreate
	fun handleCreate(group: PlayerGroup) {
		val username: String =  SecurityContextHolder.getContext().getAuthentication().name
		println("Creating group: $group with user: $username")
		group.groupOwner = username
	}
}

/*
GolfUser is the data class a unique player id and username. It is used to link them to their groups and to messages.
It is not needed for sign in, OAuth2 authenication is handled by the okta plugin. The username for okta and for this
class ought to be the same
 */

@Entity
data class GolfUser(@Id @GeneratedValue(strategy = GenerationType.IDENTITY)  var golfUserId: Int? = null ,
					var userName: String? = null)

@RepositoryRestResource
interface UsersRepository : JpaRepository<GolfUser, Int>


/**
 * UserGroups is a table in the database that contains userIDs and groupIds, allowing a query that will return all the
 * members of a group. It will also allow logic be applied to enforce the numbers in a group.
 * The MySql table uses a composite primary key comprised of the two foreign keys. To facilitate this relationship
 * it was necessary to create two classes, the UserGroups class and the separate UserGroupsId class that
 * provides the composite ID. This took many hours to figure out!!!
 */



@Entity
data class UserGroups  (@EmbeddedId var userGroupsId: UserGroupsId? = null)

@Embeddable
class UserGroupsId (
		@Column(name = "golf_user_id_enrolled")
		var userGroupId: Int = -1,

		@Column(name = "player_group_id_enrolled")
		var playerGroupIdEnrolled: Int = -1

):Serializable

@RepositoryRestResource
interface UserGroupsRepository : JpaRepository<UserGroups, UserGroupsId> {
	// fun findAllByGolfUserIdEnrolled(userName: String): List<UserGroups>
	//   fun findAllByUserGroupsId
}

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
interface ChatMessageRepository : JpaRepository<ChatMessage, Int>


@Entity
data class TournamentGolfer(@Id @GeneratedValue(strategy = GenerationType.IDENTITY)  var tournamentGolferId: Int? = null ,
					var firstName: String? = null, var secondName: String? = null, var tournamentOddsToOne: Int? = null, var tournamentPosition : Int? = null)

@RepositoryRestResource
interface TournamentGolferRepository : JpaRepository<TournamentGolfer, Int>

