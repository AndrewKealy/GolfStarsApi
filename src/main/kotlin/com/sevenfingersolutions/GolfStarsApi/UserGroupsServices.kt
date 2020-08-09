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

    fun deleteAll() {
        userGroupsRepository.deleteAll()
    }

    fun deleteListOfUserGroups(userGroups: List<UserGroups>) {
        userGroups.forEach {
            userGroupsRepository.delete(it)
        }
    }

        fun save(userGroups: UserGroups) {
            userGroupsRepository.save(userGroups)
            userGroupsRepository.flush()
        }


        @Transactional
        fun findAllByUser(name: String): List<UserGroups>? {

            val golfUser: GolfUser = usersRepository.findByUserName(name)

            val userGroups = userGroupsRepository.findAll()
            var usersUserGroups: MutableList<UserGroups> = arrayListOf()
            userGroups.forEach {
                if (golfUser != null) if (it.userGroupsId?.golfUserIdEnrolled == golfUser.golfUserId) {
                    usersUserGroups.add(it)
                }
            }
            return usersUserGroups
        }

        @Transactional
        fun findByUserGroupsId(playerGroupsId: Int): List<UserGroups>? {

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
