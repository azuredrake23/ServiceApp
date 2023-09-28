package com.example.serviceapp.data.common.database.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.serviceapp.data.common.database.daos.*
import com.example.serviceapp.data.common.database.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [User::class, Booking::class, Service::class, Master::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun masterDao(): MasterDao
    abstract fun bookingDao(): BookingDao
    abstract fun serviceDao(): ServiceDao

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
//                    populateUserDatabase(database.userDao())
//                    populateBookingDatabase(database.bookingDao())
//                    populateMasterServiceDatabase(database.serviceDao())
//                    populateMasterDatabase(database.masterDao())
                }
            }
        }

        suspend fun populateUserDatabase(userDao: UserDao) {
            userDao.deleteAll()
        }

        suspend fun populateBookingDatabase(bookingDao: BookingDao) {
            bookingDao.deleteAll()
        }

        suspend fun populateMasterServiceDatabase(serviceDao: ServiceDao) {
            serviceDao.deleteAll()
        }

        suspend fun populateMasterDatabase(masterDao: MasterDao) {
            masterDao.deleteAll()
        }
    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context, coroutineScope: CoroutineScope
        ): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "appDatabase"
                ).addCallback(AppDatabaseCallback(coroutineScope)).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

