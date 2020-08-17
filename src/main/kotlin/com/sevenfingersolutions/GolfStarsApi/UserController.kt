package com.sevenfingersolutions.GolfStarsApi


import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.*
import java.security.Principal


@RestController
class UserController(val groupsRepository: GroupsRepository, val usersRepository: UsersRepository,
                     var userGroupsServices : UserGroupsServices, var chatMessageRepository: ChatMessageRepository,
                     var tournamentRepository : TournamentRepository, var tournamentGolferRepository : TournamentGolferRepository,
                     var tournamentEnrollmentServices: TournamentEnrollmentServices) {


//    A method that returns an array of all the player-groups of which the user is a member

    @GetMapping("/user/groups")
       fun groups(principal: Principal, groupName: String?): List<PlayerGroup> {

        println("Fetching groups for user: ${principal.name}")
        var listOfPlayerGroups : MutableList<PlayerGroup> = arrayListOf()
        if(groupName.isNullOrEmpty()) {
            try {
                val usersGroups: List<UserGroups>? = userGroupsServices.findAllByUser(principal.name)

                println("made it to here: " + usersGroups)


                if (usersGroups != null) {
                    if (usersGroups.isEmpty()) {
                        return listOf()
                    } else {
                        usersGroups.forEach {
                            val playerGroup: PlayerGroup = groupsRepository.findByPlayerGroupId(it.userGroupsId?.playerGroupIdEnrolled)
                            listOfPlayerGroups.add(playerGroup)
                            println()
                        }

                    }
                }

                println(listOfPlayerGroups.toString())
                return listOfPlayerGroups
            } catch (EmptyResultDataAccessException: Exception) {
                println("This user has no groups")

                return listOfPlayerGroups

            }
        }   else if(groupName.equals("all")) {
            return groupsRepository.findAll()
        } else {
            return groupsRepository.findAllByGroupOwnerAndGroupNameContainingIgnoringCase(principal.name, groupName)
        }

    }

    //    A method that returns an array of all the player-groups of which the user is a member



    @GetMapping("/user/userGroupses")
        fun userGroups(principal: Principal, playerGroupIdEnrolled: String?): List<UserGroups> {
        var listOfUserGroups: List<UserGroups> = arrayListOf()
        println("Fetching userGroups for user: ${principal.name} and playerGroupId: " + playerGroupIdEnrolled)
        if (playerGroupIdEnrolled.isNullOrEmpty()) {
            return try {
                println("made it to here xxx")
                listOfUserGroups = userGroupsServices.findAllGroupMembersByUser(principal.name)


                listOfUserGroups
            } catch (EmptyResultDataAccessException: Exception) {
                println("This user has no user groups")

                listOfUserGroups

            }
        } else return try {
            println("Searching by groupId: $playerGroupIdEnrolled")
     //       userGroupsServices.findallByUserAndGroupId(principal.name, playerGroupIdEnrolled)
            userGroupsServices.findByPlayerGroupsId(playerGroupIdEnrolled.toInt())!!
        } catch (EmptyResultDataAccessException: Exception) {
            println("This user has no user groups")

            listOfUserGroups

        }
    }
/*
    @GetMapping("/user/myGroups/userGroupsesByGroupId")
        fun findAllMembersById(@RequestParam id: String, principal: Principal): List<UserGroups>{
        var listOfUserGroups: List<UserGroups> = arrayListOf()

        println("Fetching userGroups for user: ${principal.name} and id: ${id}")
        println("Int of String: " + id.toInt())
        return try {
            println("made it to here xxx")
            listOfUserGroups = userGroupsServices.findAllGroupMembersByUserAndGroupId("andrewkealy@hotmail.com", id.toInt())


            listOfUserGroups
        } catch (EmptyResultDataAccessException: Exception) {
            println("This user has no user groups with that Id")

            listOfUserGroups

        }
    }
*/
    /*

     fun groups (@AuthenticationPrincipal user: OidcUser): List<PlayerGroup> {
         return groupsRepository.findAll()
     }
 */
    // A method that returns the user name and email address.
    @GetMapping("/user")
    fun user(@AuthenticationPrincipal user: OidcUser): List<String> {
        var userDetails : MutableList<String> = arrayListOf()


        userDetails.add(user.preferredUsername)
        userDetails.add(user.givenName)
        userDetails.add(user.familyName)
        return userDetails;
    }


    // A method that returns all player groups.
    @GetMapping("/playerGroups")
    fun groups (): List<PlayerGroup> {
        return groupsRepository.findAll()
    }

/*
    @RequestMapping(value = ["/userGroups/{userGroupsExportId}"], method = [RequestMethod.GET], produces = ["application/json"])
    fun getUserGroupsByExportId(@PathVariable("userGroupsExportId") userGroupsExportId: Int?): List<UserGroups> {
        val userName: String =  SecurityContextHolder.getContext().authentication.name
        var listOfUserGroups: List<UserGroups>
        return if(userGroupsExportId != null) {
            println("Fetching userGroups for user: ${userName} and exportId: $userGroupsExportId")
            listOfUserGroups = userGroupsServices.findAllGroupMembersByUserAndExportId(userName, userGroupsExportId)
            listOfUserGroups
        } else
            userGroupsServices.getAll()
    }
    // A method that returns all user groups.
*/
    @GetMapping("/userGroups")
        fun userGroups(): List<UserGroups> {
            return  userGroupsServices.getAll()
    }

}
