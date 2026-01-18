package com.example.moviesdb.di

import android.content.Context
import com.example.moviesdb.data.local.db.AppDao
import com.example.moviesdb.data.remote.TMDBApiService
import com.example.moviesdb.data.repository.AppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Provides
    @Singleton
    fun provideMovieRepository(
        apiService: TMDBApiService,
        appDao: AppDao,
        @ApplicationContext context: Context
    ): AppRepository =
        AppRepository(appDao, apiService, context)
}