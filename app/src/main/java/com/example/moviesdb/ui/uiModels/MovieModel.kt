package com.example.moviesdb.ui.uiModels

data class MovieModel(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val voteAverage: Double,
    val releaseDate: String?,
    val isBookmarked: Boolean = false
)
