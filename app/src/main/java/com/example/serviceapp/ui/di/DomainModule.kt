package com.example.serviceapp.ui.di

import com.example.serviceapp.domain.repositories.common_repository.CommonRepository
import com.example.serviceapp.domain.repositories.common_repository.usecase.PopupMessageUseCase
import com.example.serviceapp.domain.repositories.login_fragment.LoginRepository
import com.example.serviceapp.domain.repositories.login_fragment.usecase.LoginUserByGoogleUseCase
import com.example.serviceapp.domain.repositories.login_fragment.usecase.LoginUserByPhoneNumberUseCase
import com.example.serviceapp.domain.repositories.settings.SettingsRepository
import com.example.serviceapp.domain.repositories.settings.usecase.GetAppLanguageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {

    //--------------------------- Common Repository UseCases ----------------------------!>

    @Singleton
    @Provides
    fun providePopupMessageUseCase(commonRepository: CommonRepository): PopupMessageUseCase =
        PopupMessageUseCase(commonRepository)

    //    @Singleton
//    @Provides
//    fun provideNavigateUseCase(commonRepository: CommonRepository): NavigateUserUseCase = NavigateUserUseCase(commonRepository)


    //--------------------------- Settings Repository UseCases ----------------------------!>

    @Singleton
    @Provides
    fun provideGetAppLanguageUseCase(repository: SettingsRepository): GetAppLanguageUseCase =
        GetAppLanguageUseCase(repository)

    //--------------------------- Login Repository UseCases ----------------------------!>

    @Singleton
    @Provides
    fun provideLoginUserByGoogleUseCase(rep: LoginRepository): LoginUserByGoogleUseCase =
        LoginUserByGoogleUseCase(rep)

    @Singleton
    @Provides
    fun provideLoginUserByPhoneNumberUseCase(rep: LoginRepository): LoginUserByPhoneNumberUseCase =
        LoginUserByPhoneNumberUseCase(rep)

}