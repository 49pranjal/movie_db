package com.example.moviesdb.ui.uiModels

data class BookmarkUiState(
    val movies: List<MovieModel> = emptyList(),
    val isEmpty: Boolean = false
)
