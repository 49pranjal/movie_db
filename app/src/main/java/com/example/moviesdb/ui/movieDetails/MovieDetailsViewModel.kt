package com.example.moviesdb.ui.movieDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesdb.data.repository.AppRepository
import com.example.moviesdb.ui.uiModels.MovieDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailsUiState())
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            // start loading
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            val result = repository.getMovieDetails(movieId)

            if (result.isSuccess) {
                val movie = result.getOrNull()!!

                _uiState.update {
                    it.copy(
                        movie = movie,
                        isBookmarked = movie.isBookmarked,
                        isLoading = false,
                        error = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message
                            ?: "Something went wrong"
                    )
                }
            }
        }
    }

    //For bookmarking Movie
    fun onBookmarkClick() {
        val movie = _uiState.value.movie ?: return

        viewModelScope.launch {
            repository.toggleBookmarkFromDetails(movie)

            // update UI optimistically
            _uiState.update { state ->
                val currentMovie = state.movie ?: return@update state

                state.copy(
                    movie = currentMovie.copy(
                        isBookmarked = !currentMovie.isBookmarked
                    )
                )
            }
        }
    }
}