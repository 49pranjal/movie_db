package com.example.moviesdb.ui.uiModels

data class SearchUiState(
    val query: String = "",
    val results: List<MovieModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEmpty: Boolean = false,
    val isPaging: Boolean = false,
    val page: Int = 1,
    val hasReachedEnd: Boolean = false
)