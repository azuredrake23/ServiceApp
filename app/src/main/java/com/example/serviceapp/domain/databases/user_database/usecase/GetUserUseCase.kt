package com.example.serviceapp.domain.databases.user_database.usecase

import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.domain.databases.user_database.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor (private val userRepository: UserRepository){

    fun isUserExistsByPhoneNumber(phoneNumber: String): Boolean = userRepository.isUserExistsByPhoneNumber(phoneNumber)

}