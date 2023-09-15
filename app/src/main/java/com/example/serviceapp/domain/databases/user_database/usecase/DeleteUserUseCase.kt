package com.example.serviceapp.domain.databases.user_database.usecase

import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.domain.databases.user_database.UserRepository
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor (private val userRepository: UserRepository) {

    suspend fun delete(email: String? = null, phoneNumber: String? = null) {
        userRepository.delete(email, phoneNumber)
    }
}