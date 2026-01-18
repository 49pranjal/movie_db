package com.example.moviesdb.data.remote

import com.example.moviesdb.data.remote.dtoModels.MovieDetailsResponse
import com.example.moviesdb.data.remote.dtoModels.MovieListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBApiService {

    @GET("movie/now_playing")
    suspend fun getNowPlaying(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String
    ): MovieListResponse

    @GET("trending/movie/week")
    suspend fun getTrending(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String
    ): MovieListResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): MovieDetailsResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("api_key") apiKey: String
    ): MovieListResponse
}