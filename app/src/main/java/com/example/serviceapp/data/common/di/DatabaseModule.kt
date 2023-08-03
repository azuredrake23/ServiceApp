package com.example.serviceapp.data.common.di

import android.content.Context
import com.example.serviceapp.data.common.database.daos.*
import com.example.serviceapp.data.domain.databases.user_database.UserRepositoryImpl
import com.example.serviceapp.data.common.database.database.AppDatabase
import com.example.serviceapp.data.domain.databases.master_database.MasterRepositoryImpl
import com.example.serviceapp.data.domain.databases.order_database.BookingRepository
import com.example.serviceapp.data.domain.databases.order_database.BookingRepositoryImpl
import com.example.serviceapp.data.domain.databases.service_database.ServiceRepository
import com.example.serviceapp.data.domain.databases.service_database.ServiceRepositoryImpl
import com.example.serviceapp.data.domain.databases.user_database.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase = AppDatabase.getDatabase(context, CoroutineScope(SupervisorJob() + Dispatchers.Main))

    @Provides
    @Singleton
    fun provideMasterDao(database: AppDatabase): MasterDao = database.masterDao()

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    @Singleton
    fun provideBookingDao(database: AppDatabase): BookingDao = database.bookingDao()

    @Provides
    @Singleton
    fun provideServiceDao(database: AppDatabase): ServiceDao = database.serviceDao()

    @Provides
    @Singleton
    fun provideMasterDaoRepository(masterDao: MasterDao): MasterRepositoryImpl = MasterRepositoryImpl(masterDao)

    @Provides
    @Singleton
    fun provideUserDaoRepository(userDao: UserDao): UserRepository = UserRepositoryImpl(userDao)

    @Provides
    @Singleton
    fun provideBookingDaoRepository(bookingDao: BookingDao): BookingRepository = BookingRepositoryImpl(bookingDao)

    @Provides
    @Singleton
    fun provideServiceDaoRepository(serviceDao: ServiceDao): ServiceRepository = ServiceRepositoryImpl(serviceDao)

}