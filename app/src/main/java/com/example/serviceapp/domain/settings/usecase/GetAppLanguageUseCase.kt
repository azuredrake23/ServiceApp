package com.example.serviceapp.domain.settings.usecase

import com.example.serviceapp.domain.settings.SettingsRepository
import javax.inject.Inject

class GetAppLanguageUseCase @Inject constructor(private val repository: SettingsRepository) {

    suspend fun execute(): String = repository.getAppLanguage()
}