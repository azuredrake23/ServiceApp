package com.example.serviceapp.data.domain.databases.user_database.usecase

import com.example.serviceapp.data.domain.databases.user_database.UserRepository
import javax.inject.Inject

class DeleteAllUsersUseCase @Inject constructor (private val userRepository: UserRepository) {

    suspend fun deleteAll() {
        userRepository.deleteAll()
    }
}