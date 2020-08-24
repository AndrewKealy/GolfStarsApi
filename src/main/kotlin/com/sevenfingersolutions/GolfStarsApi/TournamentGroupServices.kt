package com.sevenfingersolutions.GolfStarsApi


import javax.transaction.Transactional

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * This service is designed to connect allow composite  keys be assigned to the linked MySql database
 *
 */

@Service
public class TournamentGroupServices {

    @Autowired
    lateinit var tournamentGroupRepository: TournamentGroupRepository

    fun deleteAll(){
        tournamentGroupRepository.deleteAll()
    }

    fun save(tournamentGroup: TournamentGroup){
        tournamentGroupRepository.save(tournamentGroup)
        tournamentGroupRepository.flush()
    }

    @Transactional
    fun getAll(): MutableList<TournamentGroup> {
        val tournamentGroup = tournamentGroupRepository.findAll()
    //    tournamentGroup.forEach{println(it)}
        return tournamentGroup
    }

    @Transactional
    fun findAllByPlayerGroupId(playerGroupIdEnrolled: Int?): List<TournamentGroup>{
        val tournamentGroups = tournamentGroupRepository.findAll()
        val tournamentGroupsById: MutableList<TournamentGroup> = arrayListOf()
        tournamentGroups.forEach {
            if (it.tournamentGroupId?.playerGroupForeignId == playerGroupIdEnrolled) {
                tournamentGroupsById.add(it)
            }
        }
        return tournamentGroups
    }
}
