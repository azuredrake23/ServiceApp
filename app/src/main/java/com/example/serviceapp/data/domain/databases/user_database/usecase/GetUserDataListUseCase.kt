package com.example.serviceapp.data.domain.databases.user_database.usecase

import androidx.lifecycle.LiveData
import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.data.domain.databases.user_database.UserRepository
import javax.inject.Inject

class GetUserDataListUseCase @Inject constructor (userRepository: UserRepository){

    val userDataList: LiveData<List<User>> = userRepository.getUserDataList()
}