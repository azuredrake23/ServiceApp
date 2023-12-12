package com.example.serviceapp.domain.repositories.login_fragment.usecase

import com.example.serviceapp.domain.repositories.login_fragment.LoginRepository
import javax.inject.Inject

class LoginUserByGoogleUseCase @Inject constructor(private val rep: LoginRepository) {

    fun execute(){
        rep.loginUserByGoogle()
    }
}