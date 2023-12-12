package com.example.serviceapp.domain.repositories.settings.usecase

import android.service.autofill.UserData
import com.example.serviceapp.domain.repositories.settings.SettingsRepository
import javax.inject.Inject

class GetAppLanguageUseCase @Inject constructor(private val repository: SettingsRepository) {

    suspend fun execute(): String = repository.getAppLanguage()
}