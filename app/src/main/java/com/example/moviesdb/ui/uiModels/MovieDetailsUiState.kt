package com.example.moviesdb.ui.uiModels

data class MovieDetailsUiState(
    val movie: MovieDetailsModel? = null,
    val isBookmarked: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)