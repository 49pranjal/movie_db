package com.example.moviesdb.ui.bookmark

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesdb.data.repository.AppRepository
import com.example.moviesdb.ui.uiModels.BookmarkUiState
import com.example.moviesdb.ui.uiModels.MovieModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    val uiState: StateFlow<BookmarkUiState> =
        repository.observeBookmarkedMovies()
            .map { movies ->
                BookmarkUiState(
                    movies = movies,
                    isEmpty = movies.isEmpty()
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                BookmarkUiState()
            )

    fun onUnbookmark(movie: MovieModel) {
        viewModelScope.launch {
            repository.toggleBookmark(movie.id)
        }
    }
}