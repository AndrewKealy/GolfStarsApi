package com.sevenfingersolutions.GolfStarsApi

import org.apache.catalina.User
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
    fun deleteAll(){
        userGroupsRepository.deleteAll()
    }

    fun save(userGroups: UserGroups){
        userGroupsRepository.save(userGroups)
        userGroupsRepository.flush()
    }
    @Transactional
    fun findAllByUser(name: String): List<UserGroups>? {

        val golfUser :GolfUser = usersRepository.findByUserName(name)

        val userGroups = userGroupsRepository.findAll()
        var usersUserGroups : MutableList<UserGroups> = arrayListOf()
        userGroups.forEach {
            if (golfUser != null) if(it.userGroupsId?.golfUserIdEnrolled  == golfUser.golfUserId) {
                    usersUserGroups.add(it)
                }
        }
        return usersUserGroups
    }





    @Transactional
    fun getAll(): MutableList<UserGroups> {
        val userGroups = userGroupsRepository.findAll()
        userGroups.forEach{println(it)}
        return userGroups
    }
}
