package com.sevenfingersolutions.GolfStarsApi

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class DataInitializer(val groupsRepository: GroupsRepository, val usersRepository: UsersRepository, var userGroupsServices : UserGroupsServices, userGroupsRepository: UserGroupsRepository) : ApplicationRunner {

    @Throws(Exception::class)
    override fun run(args: ApplicationArguments) {
        listOf("Group 1", "Group 2", "Group 3").forEach {
            groupsRepository.save(PlayerGroup(groupName = it, groupOwner = "testUser", totalNumberOfPlayers = 2, hasAllMembers = false))
        }
        groupsRepository.findAll().forEach { println(it) }
    }
}
