package com.example.serviceapp.data.common.di

import com.example.serviceapp.data.common.utils.PreferenceManager
import com.example.shapel.data.common.utils.ResourceManager
import com.example.serviceapp.data.settings.SettingsRepositoryImpl
import com.example.shapel.data.domain.settings.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [CommonModule::class])
@InstallIn(SingletonComponent::class)
class SettingsModule {

    @Provides
    @Singleton
    fun provideAppLanguageRepository(
        prefsManager: PreferenceManager,
        resourceManager: ResourceManager
    ): SettingsRepository = SettingsRepositoryImpl(prefsManager, resourceManager)
}