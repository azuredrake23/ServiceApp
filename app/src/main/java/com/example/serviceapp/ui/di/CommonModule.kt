package com.example.serviceapp.ui.di

import android.content.Context
import com.example.serviceapp.data.utils.PreferenceManager
import com.example.serviceapp.data.utils.ResourceManager
import com.example.serviceapp.domain.repositories.common_repository.usecase.PopupMessageUseCase
import com.example.serviceapp.domain.repositories.settings.usecase.GetAppLanguageUseCase
import com.example.serviceapp.ui.view_models.MainViewModel
import com.example.serviceapp.ui.view_models.FirebaseViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Singleton
    @Provides
    fun provideContext(@ApplicationContext context: Context) = context

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
    fun provideMainViewModel(getAppLanguageUseCase: GetAppLanguageUseCase, popupMessageUseCase: PopupMessageUseCase): MainViewModel =
        MainViewModel(getAppLanguageUseCase, popupMessageUseCase)

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