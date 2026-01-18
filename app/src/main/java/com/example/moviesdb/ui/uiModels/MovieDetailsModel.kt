package com.example.moviesdb.ui.uiModels

data class MovieDetailsModel(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val overview: String,
    val releaseDate: String?,
    val runtime: Int?,
    val voteAverage: Double,
    val voteCount: Int,
    val genres: String,
    val isBookmarked: Boolean
)
