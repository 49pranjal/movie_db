package com.example.moviesdb.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moviesdb.data.local.entity.MovieDetailsEntity
import com.example.moviesdb.data.local.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    //Queries for Home Page

    @Query("SELECT * FROM movies WHERE category = :category ORDER BY dbId")
    fun observeMoviesByCategory(category: String): Flow<List<MovieEntity>>

    @Query("SELECT id FROM movies WHERE isBookmarked = 1")
    suspend fun getBookmarkedIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Query("UPDATE movies SET isBookmarked = NOT isBookmarked WHERE id = :movieId")
    suspend fun toggleBookmark(movieId: Int)

    //Query for Bookmark Screen

    @Query("SELECT * FROM movies WHERE isBookmarked = 1 ORDER BY dbId")
    fun observeBookmarkedMovies(): Flow<List<MovieEntity>>

    //Queries for Movie Details Screen
    @Query("SELECT * FROM movie_details WHERE id = :movieId")
    suspend fun getMovieDetails(movieId: Int): MovieDetailsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieDetails(entity: MovieDetailsEntity)

    @Query("SELECT * FROM movies WHERE id = :movieId LIMIT 1")
    suspend fun getMovieByMovieId(movieId: Int): MovieEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

}