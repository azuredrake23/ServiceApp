package com.example.serviceapp.data.domain.databases.user_database.usecase

import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.data.domain.databases.user_database.UserRepository
import javax.inject.Inject

class UpdateAllUsersUseCase @Inject constructor (private val userRepository: UserRepository) {

    suspend fun updateAll(vararg users: User) {
        userRepository.updateAll(*users)
    }
}