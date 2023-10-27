package com.example.serviceapp.data.models

sealed class UserModel {
    data class UserData(var userName: String? = null, var userEmail: String? = null, var userPhone: String? = null)
    data class UserDataWithSignInState(
        val userData: UserData,
        val errorType: Int? = null
    ) : UserModel()

}