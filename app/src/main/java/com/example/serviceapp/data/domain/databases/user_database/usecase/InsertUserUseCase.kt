package com.example.serviceapp.data.domain.databases.user_database.usecase

import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.data.domain.databases.user_database.UserRepository
import javax.inject.Inject

class InsertUserUseCase @Inject constructor (private val userRepository: UserRepository){

    suspend fun insert(user: User) {
        userRepository.insert(user)
    }
}