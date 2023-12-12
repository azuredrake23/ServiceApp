package com.example.serviceapp.domain.repositories.settings

interface SettingsRepository {
    suspend fun getAppLanguage(): String
}