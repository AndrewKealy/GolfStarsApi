package com.sevenfingersolutions.GolfStarsApi

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Component

@Component
class DataInitializer(val groupsRepository: GroupsRepository, val usersRepository: UsersRepository,
                      var userGroupsRepository: UserGroupsRepository, var tournamentRepository : TournamentRepository,
                      var tournamentGolferRepository : TournamentGolferRepository, var tournamentEnrollmentServices: TournamentEnrollmentServices,
                      val tournamentGroupServices: TournamentGroupServices) : ApplicationRunner {


    @Throws(Exception::class)

/*
This class creates user, group and tournament data to populate the database on first start up.
 */
    override fun run(args: ApplicationArguments) {

        val currentTournamentId: Int = 1

        val johnSmith = GolfUser(userName = "johnsmith@fakeemail.com", firstName = "John", familyName = "Smith")
        val seanMurphy = GolfUser(userName = "seanmurphy@fakeemail.com", firstName = "Sean", familyName = "Smith")
        val maryOBrien = GolfUser(userName = "maryobrien@fakeemail.com", firstName = "Mary", familyName = "O'Brien")
        val marthaOReilly = GolfUser(userName = "marthaoreilly@fakeemail.com", firstName = "Martha", familyName = "O'Reilly")
        val jamesJameson = GolfUser(userName = "jamesjameson@fakeemail.com", firstName = "James", familyName = "Jameson")
        val ultanHijinks = GolfUser(userName = "ultanhijinks@fakeemail.com", firstName = "Ultan", familyName = "Hijinks")
        val ursulaTeeoff = GolfUser(userName = "ursulateeoff@fakeemail.com", firstName = "Ursula", familyName = "Teeoff")
        val gobnaitGreenfees = GolfUser(userName = "gobnaitgreenfees@fakeemail.com", firstName = "Gobnait", familyName = "Greenfees")

        listOf<GolfUser>(johnSmith, seanMurphy, maryOBrien, marthaOReilly, jamesJameson, ultanHijinks, ursulaTeeoff, gobnaitGreenfees).forEach{
            usersRepository.save(it)
        }
        val johnsSaints = PlayerGroup(groupName = "John's saints", totalNumberOfPlayers = 6, hasAllMembers = true, groupOwner = johnSmith.userName, isPrivate = true )
        val seansStars = PlayerGroup(groupName = "Sean's stars", totalNumberOfPlayers = 6, hasAllMembers = false, groupOwner = seanMurphy.userName, isPrivate = false)
        val marysMob = PlayerGroup(groupName = "Mary's mob", totalNumberOfPlayers = 4, hasAllMembers = false, groupOwner = maryOBrien.userName, isPrivate = true)
        val oReillyGroup = PlayerGroup(groupName = "Oh Really, O'Reilly", totalNumberOfPlayers = 4, hasAllMembers = false, groupOwner = marthaOReilly.userName, isPrivate = false)
        val jamesonGroup = PlayerGroup(groupName = "Jemmy's Gems", totalNumberOfPlayers = 4, hasAllMembers = false, groupOwner = jamesJameson.userName, isPrivate = false)
        val ultansGroup = PlayerGroup(groupName = "Ultimate Ultan", totalNumberOfPlayers = 2, hasAllMembers = false, groupOwner = ultanHijinks.userName, isPrivate = false)
        val ursulasGroup = PlayerGroup(groupName = "Ursulas group", totalNumberOfPlayers = 2, hasAllMembers = false, groupOwner = ursulaTeeoff.userName,  isPrivate = false)
        val greenfeesGroup = (PlayerGroup(groupName = "Green is for go", totalNumberOfPlayers = 1, hasAllMembers = true, groupOwner = gobnaitGreenfees.userName,  isPrivate = false))

        listOf<PlayerGroup>(johnsSaints, seansStars, marysMob, oReillyGroup, jamesonGroup, ultansGroup, ursulasGroup, greenfeesGroup).forEach {
        groupsRepository.save(it) }



/*
A function to initialize data in the UserGroupsRepository, which takes a unique key from a composite of
two foreign keys
*/

        fun saveUserGroups() {

            val userGroupsIdForJohnsGroupOne = johnSmith.golfUserId?.let { johnsSaints.playerGroupId?.let { it1 -> UserGroupsId(it, it1) } }
            val userGroupsIdForJohnsGroupTwo = seanMurphy.golfUserId?.let {johnsSaints.playerGroupId?.let{it1 -> UserGroupsId(it, it1)}}
            val userGroupsIdForJohnsGroupThree = maryOBrien.golfUserId?.let { johnsSaints.playerGroupId?.let { it1 -> UserGroupsId(it, it1) } }
            val userGroupsIdForJohnsGroupFour = marthaOReilly.golfUserId?.let {johnsSaints.playerGroupId?.let{it1 -> UserGroupsId(it, it1)}}
            val userGroupsIdForJohnsGroupFive = jamesJameson.golfUserId?.let { johnsSaints.playerGroupId?.let { it1 -> UserGroupsId(it, it1) } }
            val userGroupsIdForJohnsGroupSix = ultanHijinks.golfUserId?.let {johnsSaints.playerGroupId?.let{it1 -> UserGroupsId(it, it1)}}

            val userGroupsForJohnOne = UserGroups(userGroupsId = userGroupsIdForJohnsGroupOne, groupName = johnsSaints.groupName, golfUserName = johnSmith.userName, isOwner = true)
            val userGroupsForJohnTwo = UserGroups(userGroupsId = userGroupsIdForJohnsGroupTwo, groupName = johnsSaints.groupName, golfUserName = seanMurphy.userName, isOwner = false)
            val userGroupsForJohnThree = UserGroups(userGroupsId = userGroupsIdForJohnsGroupThree, groupName = johnsSaints.groupName, golfUserName = maryOBrien.userName, isOwner = false)
            val userGroupsForJohnFour = UserGroups(userGroupsId = userGroupsIdForJohnsGroupFour, groupName = johnsSaints.groupName, golfUserName = marthaOReilly.userName, isOwner = false)
            val userGroupsForJohnFive = UserGroups(userGroupsId = userGroupsIdForJohnsGroupFive, groupName = johnsSaints.groupName, golfUserName = jamesJameson.userName, isOwner = false)
            val userGroupsForJohnSix = UserGroups(userGroupsId = userGroupsIdForJohnsGroupSix, groupName = johnsSaints.groupName, golfUserName = ultanHijinks.userName, isOwner = false)

            listOf(userGroupsForJohnOne, userGroupsForJohnTwo, userGroupsForJohnThree, userGroupsForJohnFour, userGroupsForJohnFive, userGroupsForJohnSix).forEach {
                userGroupsRepository.save(it) }

                val userGroupsIdForSeansGroupOne = johnSmith.golfUserId?.let { seansStars.playerGroupId?.let { it1 -> UserGroupsId(it, it1) } }
                val userGroupsIdForSeansGroupTwo = seanMurphy.golfUserId?.let {seansStars.playerGroupId?.let{it1 -> UserGroupsId(it, it1)}}
                val userGroupsIdForSeansGroupThree = maryOBrien.golfUserId?.let { seansStars.playerGroupId?.let { it1 -> UserGroupsId(it, it1) } }
                val userGroupsIdForSeansGroupFour = marthaOReilly.golfUserId?.let {seansStars.playerGroupId?.let{it1 -> UserGroupsId(it, it1)} }

                val userGroupsForSeanOne = UserGroups(userGroupsId = userGroupsIdForSeansGroupOne, groupName = seansStars.groupName, golfUserName = johnSmith.userName, isOwner = false)
                val userGroupsForSeanTwo = UserGroups(userGroupsId = userGroupsIdForSeansGroupTwo, groupName = seansStars.groupName, golfUserName = seanMurphy.userName, isOwner = true)
                val userGroupsForSeanThree = UserGroups(userGroupsId = userGroupsIdForSeansGroupThree, groupName = seansStars.groupName, golfUserName = maryOBrien.userName, isOwner = false)
                val userGroupsForSeanFour = UserGroups(userGroupsId = userGroupsIdForSeansGroupFour, groupName = seansStars.groupName, golfUserName = marthaOReilly.userName, isOwner = false)

                listOf(userGroupsForSeanOne,userGroupsForSeanTwo, userGroupsForSeanThree, userGroupsForSeanFour).forEach {
                    userGroupsRepository.save(it) }

            val userGroupsIdForMarysGroupOne = seanMurphy.golfUserId?.let {marysMob.playerGroupId?.let{it1 -> UserGroupsId(it, it1)}}
            val userGroupsIdForMarysGroupTwo = maryOBrien.golfUserId?.let { marysMob.playerGroupId?.let { it1 -> UserGroupsId(it, it1) } }
            val userGroupsIdForMarysGroupThree = marthaOReilly.golfUserId?.let {marysMob.playerGroupId?.let{it1 -> UserGroupsId(it, it1)} }

            val userGroupsForMarysOne = UserGroups(userGroupsId = userGroupsIdForMarysGroupOne, groupName = marysMob.groupName, golfUserName = seanMurphy.userName, isOwner = false)
            val userGroupsForMarysTwo = UserGroups(userGroupsId = userGroupsIdForMarysGroupTwo, groupName = marysMob.groupName, golfUserName = maryOBrien.userName, isOwner = true)
            val userGroupsForMarysThree = UserGroups(userGroupsId = userGroupsIdForMarysGroupThree, groupName = marysMob.groupName, golfUserName = marthaOReilly.userName, isOwner = false)

            listOf(userGroupsForMarysOne, userGroupsForMarysTwo, userGroupsForMarysThree).forEach {
                userGroupsRepository.save(it) }

            val userGroupsIdForMarthasGroupOne = marthaOReilly.golfUserId?.let {oReillyGroup.playerGroupId?.let{it1 -> UserGroupsId(it, it1)}}
            val userGroupsIdForMarthasGroupTwo = jamesJameson.golfUserId?.let { oReillyGroup.playerGroupId?.let { it1 -> UserGroupsId(it, it1) } }
            val userGroupsIdForMarthasGroupThree = ultanHijinks.golfUserId?.let {oReillyGroup.playerGroupId?.let{it1 -> UserGroupsId(it, it1)}}

            val userGroupsForMarthasOne = UserGroups(userGroupsId = userGroupsIdForMarthasGroupOne, groupName = oReillyGroup.groupName, golfUserName = marthaOReilly.userName, isOwner = true)
            val userGroupsForMarthasTwo = UserGroups(userGroupsId = userGroupsIdForMarthasGroupTwo, groupName = oReillyGroup.groupName, golfUserName = jamesJameson.userName, isOwner = false)
            val userGroupsForMarthasThree = UserGroups(userGroupsId = userGroupsIdForMarthasGroupThree, groupName = oReillyGroup.groupName, golfUserName =  ultanHijinks.userName, isOwner = false)

            listOf(userGroupsForMarthasOne, userGroupsForMarthasTwo, userGroupsForMarthasThree).forEach {
                userGroupsRepository.save(it) }

            val userGroupsIdForJamesesGroupOne = jamesJameson.golfUserId?.let { jamesonGroup.playerGroupId?.let { it1 -> UserGroupsId(it, it1) } }
            val userGroupsIdForJamesesGroupTwo = ultanHijinks.golfUserId?.let {jamesonGroup.playerGroupId?.let{it1 -> UserGroupsId(it, it1)}}
            val userGroupsIdForJamesesGroupThree = ursulaTeeoff.golfUserId?.let {jamesonGroup.playerGroupId?.let{it1 -> UserGroupsId(it, it1)}}

            val userGroupsForJamesGroupOne = UserGroups(userGroupsId = userGroupsIdForJamesesGroupOne, groupName = jamesonGroup.groupName, golfUserName = jamesJameson.userName, isOwner = true)
            val userGroupsForJamesGroupTwo = UserGroups(userGroupsId = userGroupsIdForJamesesGroupTwo, groupName = jamesonGroup.groupName, golfUserName = ultanHijinks.userName, isOwner = false)
            val userGroupsForJamesGroupThree = UserGroups(userGroupsId = userGroupsIdForJamesesGroupThree, groupName = jamesonGroup.groupName, golfUserName = ursulaTeeoff.userName, isOwner = false)

            listOf(userGroupsForJamesGroupOne, userGroupsForJamesGroupTwo, userGroupsForJamesGroupThree).forEach {
                userGroupsRepository.save(it) }

            val userGroupsIdForUltansGroupOne = ultanHijinks.golfUserId?.let {ultansGroup.playerGroupId?.let{it1 -> UserGroupsId(it, it1)}}
            val userGroupsForUltansGroupOne = UserGroups(userGroupsId = userGroupsIdForUltansGroupOne, groupName = ultansGroup.groupName, golfUserName = ultanHijinks.userName, isOwner = true)


            val userGroupsIdForUrsulasGroupOne = ursulaTeeoff.golfUserId?.let {ursulasGroup.playerGroupId?.let{it1 -> UserGroupsId(it, it1)}}
            val userGroupsForUrsulasGroupOne = UserGroups(userGroupsId = userGroupsIdForUrsulasGroupOne, groupName = ursulasGroup.groupName, golfUserName = ursulaTeeoff.userName, isOwner = true)

            val userGroupsIdForGobnaitsGroupOne = gobnaitGreenfees.golfUserId?.let {greenfeesGroup.playerGroupId?.let{it1 -> UserGroupsId(it, it1)}}
            val userGroupsForGobnaitsGroupOne = UserGroups(userGroupsId = userGroupsIdForGobnaitsGroupOne, groupName = greenfeesGroup.groupName, golfUserName = gobnaitGreenfees.userName, isOwner = true)

            listOf(userGroupsForUltansGroupOne, userGroupsForUrsulasGroupOne, userGroupsForGobnaitsGroupOne).forEach {
                userGroupsRepository.save(it) }

        }

/*
        There was going to be a chat function but unfortunately it wasn't possible to implement it.

        val chatMessage1: ChatMessage = ChatMessage(playerUserName = "john.smith@fakeemail.com", groupId = 1, messageBody = "This is a test message")
        chatMessageRepository.save(chatMessage1)
*/
        val tournament1 :Tournament = Tournament(tournamentName = "Wyndham Championship", tournamentLocation = "Greensboro, North Carolina", tournamentStartDate = "August 13, 2020" , tournamentEndDate = "August 16, 2020")
        tournamentRepository.save(tournament1)
        val tournament2 :Tournament = Tournament(tournamentName = "Zozo Championship", tournamentLocation = "Chiba, Japan", tournamentStartDate = "October 22, 2020" , tournamentEndDate = "October 25, 2020")
        tournamentRepository.save(tournament2)

        class GolferObject (
                val firstName: String,
                val secondName: String,
                val imageUrl: String,
                val nationality: String,
                val oddsToOne: Double,
                val tournamentPosition: Int,
                val finalScore: Int
        )

        val golfersToLoad: List<GolferObject> = listOf(GolferObject("Jim", "Herman", "toCome", "USA", 45.0, 1, -21),
                GolferObject("Billy","Horschel", "toCome","USA", 5.0, 2, -20),
                GolferObject("Kevin", "Kisner","toCome", "USA", 50.0,3, -18),
                GolferObject("Webb","Simpson","toCome","USA",11.0,3, -18),
                GolferObject("Doc","Redman", "toCome","USA",5.0,3, -18),
                GolferObject("Si Woo", "Kim","toCome", "South Korea", 1.35, 3, -18 ),
                GolferObject("Zach","Johnson","toCome","USA", 65.0, 7, -17),
                GolferObject("Harold", "Varner III", "toCome", "USA", 50.0, 7, -17),
                GolferObject("Danny", "McCarthy", "toCome", "USA",65.0, 9, -16),
                GolferObject("Patrick","Reed", "toCome", "USA", 65.0, 9, -16),
                GolferObject("Russell", "Henley", "toCome", "USA", 65.0, 9, -16),
                GolferObject("Sungae", "Im", "toCome", "South Korea", 65.0, 9, -16),
                GolferObject("Sam", "Burns", "toCome", "USA", 65.0, 13, -15),
                GolferObject("Tyler", "Duncan", "toCome", "USA", 65.0, 13, -15 ),
                GolferObject("Camreon", "Davis", "toCome","Australia", 65.0, 15, -14),
                GolferObject("Bud", "Caudley", "toCome", "USA", 65.0, 15, -14),
                GolferObject("Jason", "Kokrak", "toCome","USA", 65.0, 15, -14),
                GolferObject("Mark", "Hubbard", "toCome","USA", 20.0, 15, -14),
                GolferObject("Rob", "Oppeneheim", "toCome","USA", 12.0, 15, -14 ),
                GolferObject("Chris", "Baker", "USA", "toCome", 65.0, 20, -13),
                GolferObject("Dylan", "Fritelli", "toCome","South Africa", 65.0,20, -13),
                GolferObject ("Peter", "Malnat", "toCome", "USA", 65.0, 20, -13),
                GolferObject("Shane", "Lowry", "toCome","Ireland", 65.0, 23, -12),
                GolferObject("Harris", "English", "toCome", "USA", 65.0, 23, -12),
                GolferObject ("Ryan", "Armour", "toCome","USA", 65.0, 25, -11),
                GolferObject("Talor","Gooch", "toCome","USA", 65.0, 25, -11),
                GolferObject("Seamus", "Power","toCome", "Ireland", 65.0,27, -10),
                GolferObject("Brian","Harman","toCome","USA",65.0,27, -10),
                GolferObject("Tom","Hogue", "toCome","USA",65.0,27, -10),
                GolferObject("Will", "Gordon","toCome", "USA", 65.0, 27, -10 ),
                GolferObject("Ryan","Brehm","toCome","USA", 65.0, 31, -9),
                GolferObject("Wesley", "Bryan", "toCome", "USA", 65.0, 31, -9),
                GolferObject("Adam", "Long", "toCome", "USA",65.0, 31, -9),
                GolferObject("Paul","Casey", "toCome", "England", 65.0, 31, -9),
                GolferObject("Jason", "Dufner", "toCome", "USA", 65.0, 31, -9),
                GolferObject("Roger", "Sloan", "toCome", "Canada", 65.0, 31, -9),
                GolferObject("Matt", "Jones", "toCome", "Australia", 65.0, 37, -8),
                GolferObject("Christian", "Bezuidenhout", "toCome", "South Africa", 65.0, 37, -8 ),
                GolferObject("Vincent", "Whaley", "toCome","USA", 65.0, 37, -8),
                GolferObject("Rafa", "Cabera", "toCome", "Spain", 65.0, 37, -8),
                GolferObject("Kristoffer", "Ventura", "toCome","Norway", 65.0, 37, -8),
                GolferObject("Scott", "Stallings", "toCome","USA", 65.0, 42, -7),
                GolferObject("Matthew", "NeSmith", "toCome","USA", 65.0, 42, -7 ),
                GolferObject("Scott", "Brown", "USA", "toCome", 65.0, 42, -7),
                GolferObject("Matthias", "Schwab", "toCome","Austria", 65.0,42, -7),
                GolferObject ("Hank", "Lebioda", "toCome", "USA", 65.0, 42, -7),
                GolferObject("Andrew", "Landry", "toCome","USA", 65.0, 42, -7),
                GolferObject("Brendt", "Snedeker", "toCome", "USA", 65.0, 42,-7),
                GolferObject ("Joaquin", "Niemann", "toCome","Chile", 65.0, 42, -7),
                GolferObject ("Bo", "Hoag", "toCome","USA", 65.0, 42, -7)
        )

        golfersToLoad.forEach {
            tournamentGolferRepository.save(TournamentGolfer(firstName = it.firstName, secondName = it.secondName, imageUrl = it.imageUrl, nationality = it.nationality))}

        saveUserGroups()
// val allUserGroups: List<UserGroups> = userGroupsServices.getAll()



/*
After the golfers are loaded, they are each enrolled in the current tournament. This means that the same golfer data
can be used for multiple tournaments
 */

        fun saveTournamentEnrollment() {

            golfersToLoad.forEachIndexed {index, it ->

                val tournamentEnrollmentId = TournamentEnrollmentId(index+1, currentTournamentId)
                val tournamentEnrollment = TournamentEnrollment(tournamentEnrollmentId = tournamentEnrollmentId, tournamentIdSearch = currentTournamentId,
                        golferFirstName =  it.firstName, golferSecondName = it.secondName,
                        golferImageUrl = it.imageUrl, golferNationality = it.nationality,
                oddsToOne = it.oddsToOne, tournamentPosition = it.tournamentPosition, finalScore = it.finalScore)
                tournamentEnrollmentServices.save(tournamentEnrollment)
            }
        }

        saveTournamentEnrollment()
/*
Tournament groups are created, linking the groups of golfers with groups of game players
 */

        fun saveTournamentGroup() {

            val playerGroups : List<PlayerGroup> = groupsRepository.findAll()
            val tournamentEnrollments : List<TournamentEnrollment> = tournamentEnrollmentServices.getAll()
            playerGroups.forEach {it ->
                val playerGroupId : Int? = it.playerGroupId
                tournamentEnrollments.forEachIndexed { index, it1 ->
                    if(playerGroupId !=null) {
                        val tournamentGroupId: TournamentGroupId? = it1.tournamentEnrollmentPrimaryKey?.let { it2 -> TournamentGroupId(it2, playerGroupId) }
                        val playerGroupFilter: Int? = tournamentGroupId?.playerGroupForeignId
                        val golferId : Int? = it1.tournamentEnrollmentId?.tournamentGolferIdEnrolled
                        val golfer : TournamentGolfer? = golferId?.let { it2 -> tournamentGolferRepository.getOne(it2) }
                        val tournamentGroup = TournamentGroup(tournamentGroupId = tournamentGroupId, playerGroupFilter = playerGroupFilter, golferId = golferId,
                                golferFirstName = golfer?.firstName, golferSecondName = golfer?.secondName,
                                golferImageUrl = golfer?.imageUrl, golferNationality = golfer?.nationality, oddsToOne = it1.oddsToOne,
                                tournamentPosition = golfersToLoad[index].tournamentPosition, finalScore = golfersToLoad[index].finalScore, pickedById = -1, pickedByUsername = " ", isPicked = false
                                )
                        tournamentGroupServices.save(tournamentGroup)
                    }
                }
            }

        }

        saveTournamentGroup()
    }

}


