package com.example.serviceapp.data.domain.databases.user_database.usecase

import com.example.serviceapp.data.domain.databases.user_database.UserRepository
import javax.inject.Inject

class IsUserRegisteredUseCase @Inject constructor (private val userRepository: UserRepository){

    suspend fun setUserRegisteredState(email: String, password: String) = userRepository.setUserRegisteredState(email, password)
}