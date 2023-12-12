package com.example.serviceapp.data.repositoryImpl

import android.content.Context
import com.example.serviceapp.data.utils.DataExtensions.showToast
import com.example.serviceapp.domain.repositories.common_repository.CommonRepository

class CommonRepositoryImpl (private val context: Context): CommonRepository {

   override fun popupMessage(message: String) {
       showToast(context, message)
    }
}