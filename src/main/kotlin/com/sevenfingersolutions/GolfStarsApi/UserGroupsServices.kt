package com.sevenfingersolutions.GolfStarsApi

import javax.transaction.Transactional

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


/**
 * This service is designed to connect allow composite primary keys be assigned to the linked MySql database
 *
 */
@Service
public class UserGroupsServices {

    @Autowired
    lateinit var userGroupsRepository: UserGroupsRepository

    @Autowired
    lateinit var usersRepository: UsersRepository

    @Autowired
    lateinit var groupsRepository: GroupsRepository

  //  @Autowired
//    lateinit var userGroupsForExportRepository: UserGroupsForExportRepository

    fun deleteAll() {
        userGroupsRepository.deleteAll()
    }

    fun deleteListOfUserGroups(userGroups: List<UserGroups>) {
        userGroups.forEach {
            userGroupsRepository.delete(it)
        }
    }

        fun save(userGroups: UserGroups) {
            val userId : Int? = userGroups.userGroupsId?.golfUserIdEnrolled
            val golfUser : GolfUser = usersRepository.findByGolfUserId(userId)
            val userName: String? =  golfUser.userName

            val playerGroupId : Int? = userGroups.userGroupsId?.playerGroupIdEnrolled
            val playerGroup : PlayerGroup = groupsRepository.findByPlayerGroupId(playerGroupId)
            val playerGroupName : String? = playerGroup.groupName
            val playerGroupOwner : String? = playerGroup.groupOwner
            var isOwner : Boolean = false
            if (playerGroupOwner.equals(userName)) {
                isOwner = true
            }
  //          println("user groups id: ${userGroups.userGroupsId?.golfUserIdEnrolled}" + " ${userGroups.userGroupsId?.playerGroupIdEnrolled}")

                userGroups.groupName = playerGroupName
                userGroups.golfUserName = userName
                userGroups.isOwner = isOwner
                userGroupsRepository.save(userGroups)
                userGroupsRepository.flush()

          }




        //    val testUserGroups : UserGroups = userGroupsRepository.findByUserGroupsId(userGroups.userGroupsId)
     //       println("Expecting 6 first : " +  testUserGroups)
       //     userGroups.userGroupsId?.let { updateUserGroupsForExport(it) }



//            val userGroupsForExport : UserGroupsForExport = UserGroupsForExport(exportId = userGroups.user_groups_exportId ,groupName = playerGroupName, golfUserName = userName, isOwner = isOwner)
  //          userGroupsForExportRepository.save(userGroupsForExport)


/*
        fun updateUserGroupsForExport(userGroupsId: UserGroupsId) {
            val userGroups : UserGroups = userGroupsRepository.findByUserGroupsId(userGroupsId)
            println("Expecting 6 : " +  userGroups)
            val userGroupsForExport: UserGroupsForExport = UserGroupsForExport(exportId = userGroups.userGroupsExportId, groupName = userGroups.groupName, golfUserName = userGroups.golfUserName, isOwner = userGroups.isOwner)
                userGroupsForExportRepository.save(userGroupsForExport)

        }
*/



        @Transactional
        fun findAllByUser(name: String): List<UserGroups>? {

            val golfUser: GolfUser? = usersRepository.findByUserName(name)
            val userGroups = userGroupsRepository.findAll()
            val usersUserGroups: MutableList<UserGroups> = arrayListOf()
            userGroups.forEach {
                if (golfUser != null) {
                    if (it.userGroupsId?.golfUserIdEnrolled == golfUser.golfUserId) {
                        usersUserGroups.add(it)
                    }
                }
            }
            return usersUserGroups

        }

    @Transactional
    fun findAllGroupMembersByUser(name: String): List<UserGroups> {

        val golfUser: GolfUser? = usersRepository.findByUserName(name)
        val userGroups = userGroupsRepository.findAll()
        val usersUserGroups: MutableList<UserGroups> = arrayListOf()
        userGroups.forEach { userGroups1 ->
            if (golfUser != null) {
                if ( userGroups1.userGroupsId?.golfUserIdEnrolled == golfUser.golfUserId) {
                    userGroups.forEach {
                        if (it.userGroupsId?.playerGroupIdEnrolled == userGroups1.userGroupsId?.playerGroupIdEnrolled)
                            usersUserGroups.add(it)
                    }

                }
            }
        }
        return usersUserGroups

    }



    @Transactional
    fun findallByUserAndGroupId(userName: String, groupId : String): List<UserGroups> {
        println("Got correct name:$userName")
        println("Got correct name:$groupId")
        val usersUserGroups : List<UserGroups> = findAllGroupMembersByUser(userName)
        val userAndExportIdUserGroups: MutableList<UserGroups> = arrayListOf()
        usersUserGroups.forEach {
            if (it.userGroupsId?.playerGroupIdEnrolled == groupId.toInt()) {
                userAndExportIdUserGroups.add(it)
            }
        }
        return userAndExportIdUserGroups
    }



        @Transactional
        fun findByPlayerGroupsId(playerGroupsId: Int): List<UserGroups>? {

            val userGroups = userGroupsRepository.findAll()
            var userGroupsById: MutableList<UserGroups> = arrayListOf()
            userGroups.forEach {
                if (it.userGroupsId?.playerGroupIdEnrolled == playerGroupsId) {
                        userGroupsById.add(it)
                    }
            }
            return userGroupsById
        }


        @Transactional
        fun getAll(): MutableList<UserGroups> {
            val userGroups = userGroupsRepository.findAll()
            userGroups.forEach { println(it) }
            return userGroups
        }


}
