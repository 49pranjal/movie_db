package com.example.moviesdb.data.local.db

import androidx.room.Database
import androidx.room.Entity
import androidx.room.RoomDatabase
import com.example.moviesdb.data.local.entity.MovieDetailsEntity
import com.example.moviesdb.data.local.entity.MovieEntity

@Database(entities = [MovieEntity::class, MovieDetailsEntity::class],
    version = 1,
    exportSchema = false)
abstract class AppDb: RoomDatabase() {

    abstract fun appDao(): AppDao
}