package com.example.serviceapp.domain.settings

import com.example.serviceapp.R
import com.example.serviceapp.data.common.utils.PreferenceManager
import com.example.serviceapp.data.common.utils.ResourceManager
import com.example.serviceapp.domain.settings.SettingsRepository

class SettingsRepositoryImpl(
    private val prefsManager: PreferenceManager,
    private val resourceManager: ResourceManager
) : SettingsRepository {

    override suspend fun getAppLanguage(): String {
        val key = resourceManager.getString(R.string.key_language_header)
        return prefsManager.get(key, "")
    }
}