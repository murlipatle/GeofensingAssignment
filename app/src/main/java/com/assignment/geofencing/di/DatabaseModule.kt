package com.assignment.geofencing.di

import android.content.Context
import androidx.room.Room
import com.assignment.geofencing.roomdb.AppDatabase
import com.assignment.geofencing.roomdb.dao.UserDataDao
import com.assignment.geofencing.utils.Constants.Companion.ROOM_DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun provideUserDataDao(appDatabase: AppDatabase): UserDataDao {
        return appDatabase.userDataDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            ROOM_DB_NAME
        ).build()
    }

}