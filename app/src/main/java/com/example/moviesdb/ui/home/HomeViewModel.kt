package com.example.moviesdb.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesdb.data.repository.AppRepository
import com.example.moviesdb.ui.uiModels.HomeUiState
import com.example.moviesdb.ui.uiModels.MovieModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AppRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var trendingPage = 1
    private var nowPlayingPage = 1


    var isNowPlayingLoading = false
    var isTrendingLoading = false

    init {
        observeHomeData()
        loadInitialData()
    }

    private fun observeHomeData() {
        viewModelScope.launch {
            combine(
                repository.observeTrending(),
                repository.observeNowPlaying()
            ) { trending, nowPlaying ->
                _uiState.value.copy(
                    trending = trending,
                    nowPlaying = nowPlaying,
                    isLoading = if (trending.isEmpty() && nowPlaying.isEmpty()) true else false
                )
            }.collect {
                _uiState.value = it
            }
        }

    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Api is getting called in async
            val trendingDeferred = async {
                repository.fetchTrendingFromApi(trendingPage)
            }
            val nowPlayingDeferred = async {
                repository.fetchNowPlayingFromApi(nowPlayingPage)
            }

            val trendingResult = trendingDeferred.await()
            val nowPlayingResult = nowPlayingDeferred.await()

            trendingPage++
            nowPlayingPage++

            if (trendingResult.isFailure && nowPlayingResult.isFailure) {
                //BOTH failed
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = trendingResult.exceptionOrNull()?.message
                            ?: "Unable to load movies"
                    )
                }
            } else {
                // At least one succeeded
                _uiState.update {
                    it.copy(isLoading = false, error = null)
                }
            }
        }
    }

    //For Pagination

    fun loadMoreTrending() {
        if (isTrendingLoading) return
        isTrendingLoading = true

        viewModelScope.launch {
            repository.fetchTrendingFromApi(trendingPage)
            trendingPage++
            isTrendingLoading = false
        }
    }

    fun loadMoreNowPlaying() {
        if (isNowPlayingLoading) return
        isNowPlayingLoading = true

        viewModelScope.launch {
            repository.fetchNowPlayingFromApi(nowPlayingPage)
            nowPlayingPage++
            isNowPlayingLoading = false
        }
    }


    fun onBookmarkClick(movieModel: MovieModel) {
        viewModelScope.launch {
            repository.toggleBookmark(movieModel.id)
        }
    }
}