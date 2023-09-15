package com.example.serviceapp.domain.databases.user_database.usecase

import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.domain.databases.user_database.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val userRepository: UserRepository){
    suspend fun getUser(email: String? = null, phoneNumber: String? = null): User? =
        userRepository.getUser(email, phoneNumber)
}