package com.psildave.punchtheclock.di

import android.content.Context
import com.psildave.punchtheclock.data.local.PunchDatabase
import com.psildave.punchtheclock.data.local.dao.OfflinePunchDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PunchDatabase {
        return PunchDatabase.getDatabase(context)
    }

    @Provides
    fun provideOfflinePunchDao(database: PunchDatabase): OfflinePunchDao {
        return database.offlinePunchDao()
    }
}
