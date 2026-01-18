package com.example.moviesdb.data.remote.dtoModels

data class MovieListResponse(
    val page: Int,
    val results: List<MovieDto>,
    val total_pages: Int
)

data class MovieDto(
    val id: Int,
    val title: String,
    val poster_path: String?,
    val vote_average: Double,
    val release_date: String?
)
