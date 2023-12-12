package com.example.serviceapp.domain.repositories.common_repository.usecase

import com.example.serviceapp.domain.repositories.common_repository.CommonRepository
import javax.inject.Inject

class PopupMessageUseCase @Inject constructor(private val rep: CommonRepository){

    fun execute(message: String){
        rep.popupMessage(message)
    }
}