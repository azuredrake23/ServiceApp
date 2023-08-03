package com.example.serviceapp.data.domain.databases.user_database.usecase

import com.example.serviceapp.data.domain.databases.user_database.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor (private val userRepository: UserRepository){

    suspend fun getUser(userId: Int) {
        userRepository.getUser(userId)
    }
}