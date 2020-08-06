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

    fun deleteAll(){
        userGroupsRepository.deleteAll()
    }

    fun save(userGroups: UserGroups){
        userGroupsRepository.save(userGroups)
        userGroupsRepository.flush()
    }

    @Transactional
    fun showAll(): MutableList<UserGroups> {
        val userGroups = userGroupsRepository.findAll()
        // userGroups.forEach{println(it)}
        return userGroups
    }
}
