package com.example.shapel.data.domain.settings

interface SettingsRepository {
    suspend fun getAppLanguage(): String
}