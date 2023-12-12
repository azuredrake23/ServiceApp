package com.example.serviceapp.domain.repositories.login_fragment

interface LoginRepository {
    fun loginUserByGoogle()
    fun loginUserByPhoneNumber()
}