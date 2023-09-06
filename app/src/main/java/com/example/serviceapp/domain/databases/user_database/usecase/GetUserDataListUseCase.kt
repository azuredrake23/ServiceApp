package com.example.serviceapp.domain.databases.user_database.usecase

import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.domain.databases.user_database.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserDataListUseCase @Inject constructor (userRepository: UserRepository){

    val userDataList: Flow<List<User>> = userRepository.getUsersDataList()
}