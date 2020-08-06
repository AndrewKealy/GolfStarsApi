package com.sevenfingersolutions.GolfStarsApi

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class DataInitializer(val groupsRepository: GroupsRepository, val usersRepository: UsersRepository,
                      var userGroupsServices : UserGroupsServices, var chatMessageRepository: ChatMessageRepository,var tournamentRepository : TournamentRepository, var tournamentGolferRepository : TournamentGolferRepository, var tournamentEnrollmentServices: TournamentEnrollmentServices) : ApplicationRunner {


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

        /*
        A function to initialize data in the UserGroupsRepository, which takes its primary key from a composite of
        two foreign keys
         */
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


        val allUserGroups: MutableList<UserGroups> = userGroupsServices.getAll()
        var chatMessage1: ChatMessage = ChatMessage(playerUserName = "testUser1", groupId = 1, messageBody = "This is a test message")
        chatMessageRepository.save(chatMessage1)

        var tournament1 :Tournament = Tournament(tournamentName = "Houston Open", tournamentLocation = "Houston, Texas", tournamentStartDate = "November 5, 2020" , tournamentEndDate = "November 8, 2020")
        tournamentRepository.save(tournament1)
        var tournament2 :Tournament = Tournament(tournamentName = "Zozo Championship", tournamentLocation = "Chiba, Japan", tournamentStartDate = "October 22, 2020" , tournamentEndDate = "October 25, 2020")
        tournamentRepository.save(tournament2)

        var tournamentGolfer1 : TournamentGolfer = TournamentGolfer(firstName = "Robert", secondName = "Allenby", imageUrl = "toCome", nationality = "Australian")
        tournamentGolferRepository.save(tournamentGolfer1)
        var tournamentGolfer2 : TournamentGolfer = TournamentGolfer(firstName = "Byeong", secondName = "An", imageUrl = "toCome", nationality = "South Korean")
        tournamentGolferRepository.save(tournamentGolfer2)


        saveUserGroups()



        fun saveTournamentEnrollment() {
            val tournamentEnrollmentIdOne = tournamentGolfer1.tournamentGolferId?.let{tournament1.tournamentId?.let{ it1 -> TournamentEnrollmentId(it, it1)}}
            val tournamentEnrollmentOne = TournamentEnrollment(tournamentEnrollmentIdOne, 0, 0)
            tournamentEnrollmentServices.save(tournamentEnrollmentOne)

            val tournamentEnrollmentIdTwo = tournamentGolfer1.tournamentGolferId?.let{tournament2.tournamentId?.let{ it1 -> TournamentEnrollmentId(it, it1)}}
            val tournamentEnrollmentTwo = TournamentEnrollment(tournamentEnrollmentIdTwo, 2, 0)
            tournamentEnrollmentServices.save(tournamentEnrollmentTwo)

            val tournamentEnrollmentIdThree = tournamentGolfer2.tournamentGolferId?.let{tournament2.tournamentId?.let{ it1 -> TournamentEnrollmentId(it, it1)}}
            val tournamentEnrollmentThree = TournamentEnrollment(tournamentEnrollmentIdThree, 14, 0)
            tournamentEnrollmentServices.save(tournamentEnrollmentThree)
        }

        saveTournamentEnrollment()

    }
}
