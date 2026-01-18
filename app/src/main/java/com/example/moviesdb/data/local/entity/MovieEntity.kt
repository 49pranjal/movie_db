package com.example.moviesdb.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "movies",
    indices = [Index(value = ["id"], unique = true)])
data class MovieEntity(
    @PrimaryKey(autoGenerate = true)
    val dbId: Long = 0L,
    val id: Int,
    val title: String,
    val posterPath: String?,
    val voteAverage: Double,
    val releaseDate: String?,
    val category: String, // TRENDING or NOW_PLAYING
    val isBookmarked: Boolean
)