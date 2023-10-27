package com.example.serviceapp.data.common.di

import android.app.Activity
import android.content.Context
import com.example.serviceapp.data.common.utils.PreferenceManager
import com.example.serviceapp.data.common.utils.ResourceManager
import com.example.serviceapp.utils.DownloadDialog
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
class UtilsModule {

}