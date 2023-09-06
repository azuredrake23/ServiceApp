package com.example.serviceapp.domain.settings

interface SettingsRepository {
    suspend fun getAppLanguage(): String
}