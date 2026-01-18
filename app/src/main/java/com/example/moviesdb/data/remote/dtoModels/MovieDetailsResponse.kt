package com.example.moviesdb.data.remote.dtoModels

data class MovieDetailsResponse(
    val id: Int,
    val title: String,
    val poster_path: String?,
    val overview: String,
    val release_date: String?,
    val runtime: Int?,
    val vote_average: Double,
    val vote_count: Int,
    val genres: List<GenreDto>
)

data class GenreDto(
    val id: Int,
    val name: String
)