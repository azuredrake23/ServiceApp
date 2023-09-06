package com.example.serviceapp.data.common.di

import android.content.Context
import com.example.serviceapp.data.common.utils.PreferenceManager
import com.example.serviceapp.data.common.utils.ResourceManager
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
    fun providePreferenceManager(@ApplicationContext context: Context): PreferenceManager =
        PreferenceManager(context)

    @Singleton
    @Provides
    fun provideResourceManager(@ApplicationContext context: Context): ResourceManager =
        ResourceManager(context)

}