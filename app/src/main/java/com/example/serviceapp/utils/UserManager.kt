package com.example.serviceapp.utils

import android.content.Context
import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.data.entities.Errors
import com.example.serviceapp.ui.fragments.models.UserModel
import com.example.shapel.data.common.utils.ResourceManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class UserManager(
    private val appContext: Context,
    private val dispatcher: CoroutineDispatcher,
    private val appScope: CoroutineScope,
    private val resourceManager: ResourceManager
) {

    private val _userDataWithSignInState = MutableSharedFlow<UserModel.UserDataWithSignInState>()
    val userDataWithSignInState: SharedFlow<UserModel.UserDataWithSignInState> = _userDataWithSignInState

    fun setUserDataWithSignInState(
        isUserRegistered: Boolean,
        IsPasswordIncorrectAndUserExists: Boolean,
        userData: User
    ) {
        when (isUserRegistered) {
            true -> {
//                emitUserData(
//                    UserModel.UserDataWithSignInState(
//                        UserModel.UserData(userData.name, userData.email, userData.password),
//                        Errors.SIGNED_IN
//                    )
//                )
            }

            false -> {
                setIsPasswordCorrectAndUserExists(IsPasswordIncorrectAndUserExists, userData)
            }
        }
    }

    private fun setIsPasswordCorrectAndUserExists(
        IsPasswordIncorrectAndUserExists: Boolean,
        userData: User
    ) {
        when (IsPasswordIncorrectAndUserExists) {
            true -> {
//                emitUserData(
//                    UserModel.UserDataWithSignInState(
//                        UserModel.UserData(userData.name, userData.email, userData.password),
//                        Errors.SIGNED_IN_INCORRECT_PASS
//                    )
//                )
            }

            else -> {
//                emitUserData(
//                    UserModel.UserDataWithSignInState(
//                        UserModel.UserData(userData.name, userData.email, userData.password),
//                        Errors.SIGNED_IN_USER_NOT_FOUND
//                    )
//                )
            }
        }
    }

    private fun emitUserData(userData: UserModel.UserDataWithSignInState) {
        appScope.launch(dispatcher) {
            _userDataWithSignInState.emit(userData)
        }
    }
}