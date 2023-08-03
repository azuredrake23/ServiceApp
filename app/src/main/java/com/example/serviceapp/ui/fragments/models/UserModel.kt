package com.example.serviceapp.ui.fragments.models

sealed class UserModel {
    data class UserData(var userName: String? = null, var userEmail: String? = null, var userPassword: String? = null, var userState: Boolean = false)
    data class UserDataWithSignInState(
        val userData: UserData,
        val errorType: Int? = null
    ) : UserModel()

}