package com.sevenfingersolutions.GolfStarsApi


import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal


@RestController
class UserController(val groupsRepository: GroupsRepository, val usersRepository: UsersRepository,
                     var userGroupsServices : UserGroupsServices, var chatMessageRepository: ChatMessageRepository,
                     var tournamentRepository : TournamentRepository, var tournamentGolferRepository : TournamentGolferRepository,
                     var tournamentEnrollmentServices: TournamentEnrollmentServices) {


//    A methd that returns an array of all the player-groups of which the user is a member

    @GetMapping("/user/groups")

 /*
    fun groups(principal: Principal) : List<PlayerGroup> {
        println("Fetching groups for user: ${principal.name}")
        return groupsRepository.findAll()
    }
    */


       fun groups(principal: Principal): List<PlayerGroup> {
            println("Fetching groups for user: ${principal.name}")
        //val username: String =  SecurityContextHolder.getContext().getAuthentication().name
        var listOfPlayerGroups : MutableList<PlayerGroup> = arrayListOf()
        try {
            val usersGroups: List<UserGroups>? = userGroupsServices.findAllByUser(principal.name)

            println("made it to here: " + usersGroups)


            if (usersGroups != null) {
                if (usersGroups.isEmpty()) {
                    return listOf()
                } else {
                    usersGroups.forEach {
                        val playerGroup : PlayerGroup = groupsRepository.findByPlayerGroupId(it.userGroupsId?.playerGroupIdEnrolled)
                        listOfPlayerGroups.add(playerGroup)
                        println()
                    }

                }
            }

            println(listOfPlayerGroups.toString())
            return listOfPlayerGroups
        } catch (EmptyResultDataAccessException : Exception) {
            println("This user has no groups")

            return listOfPlayerGroups

        }


    }


    /*

     fun groups (@AuthenticationPrincipal user: OidcUser): List<PlayerGroup> {
         return groupsRepository.findAll()
     }
 */
    // A method that returns the user name and email address.
    @GetMapping("/user")
    fun user(@AuthenticationPrincipal user: OidcUser): MutableList<String> {
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

    fun enrolPlayerInGroup(playerGroup: PlayerGroup, userName: String) {


    }
}
