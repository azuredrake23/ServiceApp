package com.example.serviceapp.ui.di

import android.content.Context
import com.example.serviceapp.data.repositoryImpl.CommonRepositoryImpl
import com.example.serviceapp.data.repositoryImpl.LoginRepositoryImpl
import com.example.serviceapp.data.repositoryImpl.SettingsRepositoryImpl
import com.example.serviceapp.data.utils.PreferenceManager
import com.example.serviceapp.data.utils.ResourceManager
import com.example.serviceapp.domain.repositories.common_repository.CommonRepository
import com.example.serviceapp.domain.repositories.login_fragment.LoginRepository
import com.example.serviceapp.domain.repositories.settings.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Singleton
    @Provides
    fun provideCommonRepository(@ApplicationContext context: Context): CommonRepository = CommonRepositoryImpl(context)

    @Singleton
    @Provides
    fun provideAppLanguageRepository(
        prefsManager: PreferenceManager,
        resourceManager: ResourceManager
    ): SettingsRepository =
        SettingsRepositoryImpl(prefsManager, resourceManager)

    @Singleton
    @Provides
    fun provideLoginRepository(): LoginRepository = LoginRepositoryImpl()

}