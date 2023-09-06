package com.example.serviceapp.domain.databases.user_database.usecase

import com.example.serviceapp.domain.databases.user_database.UserRepository
import com.example.serviceapp.ui.common_fragments.models.UserModel
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class GetUserDataWithSignInStateUseCase @Inject constructor(userRepository: UserRepository) {

    val userDataWithSignInState: SharedFlow<UserModel.UserDataWithSignInState> = userRepository.getUserData()
}