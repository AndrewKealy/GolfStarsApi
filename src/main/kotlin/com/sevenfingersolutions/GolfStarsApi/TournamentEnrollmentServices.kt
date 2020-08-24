package com.sevenfingersolutions.GolfStarsApi


import javax.transaction.Transactional

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * This service is designed to connect allow composite  keys be assigned to the linked MySql database
 *
 */

@Service
public class TournamentEnrollmentServices {

    @Autowired
    lateinit var tournamentEnrollmentRepository: TournamentEnrollmentRepository

    fun deleteAll(){
        tournamentEnrollmentRepository.deleteAll()
    }

    fun save(tournamentEnrollment: TournamentEnrollment){
        tournamentEnrollmentRepository.save(tournamentEnrollment)
        tournamentEnrollmentRepository.flush()
    }

    @Transactional
    fun getAll(): MutableList<TournamentEnrollment> {
        val tournamentEnrollment = tournamentEnrollmentRepository.findAll()
   //   tournamentEnrollment.forEach{println(it)}
        return tournamentEnrollment
    }
}
