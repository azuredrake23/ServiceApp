package com.example.serviceapp.domain.databases.user_database.usecase

import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.domain.databases.user_database.UserRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor (private val userRepository: UserRepository) {

    suspend fun update(user: User) {
        userRepository.update(user)
    }
}