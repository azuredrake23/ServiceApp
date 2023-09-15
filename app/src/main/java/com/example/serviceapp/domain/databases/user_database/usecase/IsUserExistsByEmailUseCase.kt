package com.example.serviceapp.domain.databases.user_database.usecase

import com.example.serviceapp.domain.databases.user_database.UserRepository
import javax.inject.Inject

class IsUserExistsByEmailUseCase @Inject constructor (private val userRepository: UserRepository){
    suspend fun check(email: String) = userRepository.isUserExistsByEmail(email)
}