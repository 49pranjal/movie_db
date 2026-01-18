package com.example.moviesdb.di

import android.content.Context
import androidx.room.Room
import com.example.moviesdb.data.local.db.AppDao
import com.example.moviesdb.data.local.db.AppDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDb =
        Room.databaseBuilder(
            context,
            AppDb::class.java,
            "app_movie_db"
        )
        .build()

    @Provides
    fun provideMovieDao(db: AppDb): AppDao = db.appDao()
}