package com.example.moviesdb.ui.uiModels

data class HomeUiState(
    val trending: List<MovieModel> = emptyList(),
    val nowPlaying: List<MovieModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
