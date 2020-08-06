package com.sevenfingersolutions.GolfStarsApi

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class DataInitializer(val groupsRepository: GroupsRepository, val usersRepository: UsersRepository, var userGroupsServices : UserGroupsServices) : ApplicationRunner {

    @Throws(Exception::class)
    override fun run(args: ApplicationArguments) {
        listOf("Group 1", "Group 2", "Group 3").forEach {
            groupsRepository.save(PlayerGroup(groupName = it, groupOwner = "testUser", totalNumberOfPlayers = 2, hasAllMembers = false))
        }
        groupsRepository.findAll().forEach { println(it) }

        var testUser1: GolfUser = GolfUser(userName = "testUser1")
        var testUser2: GolfUser = GolfUser(userName = "testUser2")
        usersRepository.save(testUser1)
        usersRepository.save(testUser2)
        var testGroup1: PlayerGroup = groupsRepository.findByPlayerGroupId(1)
        println(testGroup1.toString())
        var testUserForGroup1: GolfUser = usersRepository.getOne(1)
        println(testUserForGroup1.toString())
        var testGroup2: PlayerGroup = groupsRepository.findByPlayerGroupId(2)
        var testUserForGroup2: GolfUser = usersRepository.getOne(2)
        var testGroup3: PlayerGroup = groupsRepository.findByPlayerGroupId(3)


        fun saveUserGroups() {

            val userGroupOneId = testUserForGroup1.golfUserId?.let { testGroup1.playerGroupId?.let { it1 -> UserGroupsId(it, it1) } }
            val userGroupOne = UserGroups(userGroupOneId)
            userGroupsServices.save(userGroupOne)

            val userGroupTwoId = testUserForGroup2.golfUserId?.let { testGroup2.playerGroupId?.let { it1 -> UserGroupsId(it, it1) } }
            val userGroupTwo = UserGroups(userGroupTwoId)
            userGroupsServices.save(userGroupTwo)

            val userGroupThreeId = testUserForGroup1.golfUserId?.let { testGroup3.playerGroupId?.let { it1 -> UserGroupsId(it, it1) } }
            val userGroupThree = UserGroups(userGroupThreeId)
            userGroupsServices.save(userGroupThree)

            val userGroupFourId = testUserForGroup2.golfUserId?.let { testGroup1.playerGroupId?.let { it1 -> UserGroupsId(it, it1) } }
            val userGroupFour = UserGroups(userGroupFourId)
            userGroupsServices.save(userGroupFour)

            val userGroupFiveId = testUserForGroup1.golfUserId?.let { testGroup1.playerGroupId?.let { it1 -> UserGroupsId(it, it1) } }
            val userGroupFive = UserGroups(userGroupFiveId)
            userGroupsServices.save(userGroupFive)
        }

        saveUserGroups()

        val allUserGroups: MutableList<UserGroups> = userGroupsServices.getAll()


    }
}
