package com.sevenfingersolutions.GolfStarsApi



import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal


@RestController
class UserController(val groupsRepository: GroupsRepository, val tournamentGroupServices: TournamentGroupServices,
                     val tournamentGroupRepository: TournamentGroupRepository,
                     var userGroupsServices: UserGroupsServices, var usersGroupsRepository: UserGroupsRepository) {


//    A method that returns an array of all the player-groups of which the user is a member

    @GetMapping("/user/groups")
       fun groups(principal: Principal, groupName: String?): List<PlayerGroup> {
        val listOfPlayerGroups : MutableList<PlayerGroup> = arrayListOf()
        if(groupName.isNullOrEmpty()) {
            try {
                val usersGroups: List<UserGroups>? = userGroupsServices.findAllByUser(principal.name)
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
            val listOfPublicGroups: MutableList<PlayerGroup> = arrayListOf();
            val allGroups =  groupsRepository.findAll()
            allGroups.forEach {
                if (!it.isPrivate) {
                    listOfPublicGroups.add(it)
                }
            }
            return listOfPublicGroups;
        } else {
            return groupsRepository.findAllByGroupOwnerAndGroupNameContainingIgnoringCase(principal.name, groupName)
        }

    }
    //    A method that returns an array of all the player-groups of which the user is a member



    @GetMapping("/user/userGroupses")
        fun userGroups(principal: Principal, playerGroupIdEnrolled: String?): List<UserGroups?> {
        var listOfUserGroups: List<UserGroups?> = arrayListOf()
        if (playerGroupIdEnrolled.isNullOrEmpty()) {
            return try {
                listOfUserGroups = usersGroupsRepository.findUserGroupsByGolfUserName(principal.name)
             //   listOfUserGroups = userGroupsServices.findAllGroupMembersByUser(principal.name)
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

    @GetMapping("/user/tournamentGroups")
    fun tournamentGroups(principal: Principal, playerGroupFilter: String?): List<TournamentGroup> {
        var listOfTournamentGroups: List<TournamentGroup> = arrayListOf()
        if (playerGroupFilter.isNullOrEmpty()) {
            return  listOfTournamentGroups
        }
             try {

                return tournamentGroupRepository.findAllByPlayerGroupFilter(playerGroupId = playerGroupFilter.toInt())
            } catch (EmptyResultDataAccessException: Exception) {
               return  listOfTournamentGroups

            }

    }

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
    @GetMapping("/tournamentGroups")
    fun tournamentGroups (): List<TournamentGroup> {
        return tournamentGroupServices.getAll()
    }


    // A method that returns all player groups.
    @GetMapping("/playerGroups")
    fun groups (): List<PlayerGroup> {
        return groupsRepository.findAll()
    }


    // A method that returns all user groups.

    @GetMapping("/userGroups")
        fun userGroups(): List<UserGroups> {
            return  userGroupsServices.getAll()
    }


    @GetMapping("/hello")
        fun hello(): String {
        return  "Hello"
    }


}
