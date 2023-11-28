package com.example.serviceapp.data.common.di

import android.app.Activity
import android.content.Context
import com.example.serviceapp.data.common.utils.PreferenceManager
import com.example.serviceapp.data.common.utils.ResourceManager
import com.example.serviceapp.domain.settings.SettingsRepository
import com.example.serviceapp.domain.settings.SettingsRepositoryImpl
import com.example.serviceapp.domain.settings.usecase.GetAppLanguageUseCase
import com.example.serviceapp.domain.view_models.MainViewModel
import com.example.serviceapp.domain.view_models.firebase_view_models.FirebaseViewModel
import com.example.serviceapp.ui.main.MainActivity
import com.example.serviceapp.utils.DownloadDialog
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Singleton
    @Provides
    fun providePreferenceManager(@ApplicationContext context: Context): PreferenceManager =
        PreferenceManager(context)

    @Singleton
    @Provides
    fun provideResourceManager(@ApplicationContext context: Context): ResourceManager =
        ResourceManager(context)

    @Singleton
    @Provides
    fun provideAppLanguageRepository(
        prefsManager: PreferenceManager,
        resourceManager: ResourceManager
    ): SettingsRepository = SettingsRepositoryImpl(prefsManager, resourceManager)

    @Singleton
    @Provides
    fun provideGetAppLanguageUseCase(repository: SettingsRepository): GetAppLanguageUseCase =
        GetAppLanguageUseCase(repository)

    @Singleton
    @Provides
    fun provideMainViewModel(getAppLanguageUseCase: GetAppLanguageUseCase): MainViewModel =
        MainViewModel(getAppLanguageUseCase)

    @Singleton
    @Provides
    fun provideFirebaseViewModel(
        mainViewModel: MainViewModel,
        firebaseAuth: FirebaseAuth,
        oneTapClient: SignInClient,
        resourceManager: ResourceManager,
        firebaseRealtimeDatabaseUserReference: DatabaseReference,
        signInRequest: BeginSignInRequest
    ): FirebaseViewModel = FirebaseViewModel(mainViewModel, firebaseAuth, oneTapClient, resourceManager, firebaseRealtimeDatabaseUserReference, signInRequest)
}