package com.example.serviceapp.domain.databases.user_database.usecase

import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.domain.databases.user_database.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetAllUsersUseCase @Inject constructor (userRepository: UserRepository){

    val getAllUsersUseCase: Flow<List<User>> = userRepository.getAllUsers()

}