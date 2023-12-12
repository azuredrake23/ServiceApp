package com.example.serviceapp.data.repositoryImpl

import com.example.serviceapp.data.R
import com.example.serviceapp.data.utils.PreferenceManager
import com.example.serviceapp.data.utils.ResourceManager
import com.example.serviceapp.domain.repositories.settings.SettingsRepository


class SettingsRepositoryImpl(
    private val prefsManager: PreferenceManager,
    private val resourceManager: ResourceManager
) : SettingsRepository {

    override suspend fun getAppLanguage(): String {
        val key = resourceManager.getString(R.string.key_language_header)
        return prefsManager.get(key, "")
    }
}