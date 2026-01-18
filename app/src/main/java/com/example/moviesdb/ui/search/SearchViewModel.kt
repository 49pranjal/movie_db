package com.example.moviesdb.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesdb.data.repository.AppRepository
import com.example.moviesdb.ui.uiModels.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val queryState = MutableStateFlow("")

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        observeSearchQuery()
    }

    fun onQueryChanged(query: String) {
        queryState.value = query
        _uiState.update {
            it.copy(query = query, error = null)
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch{
            queryState
                .debounce(500)   //api hit after 500 milli seconds user stops typing
                .distinctUntilChanged()       // To avoid searching same query
                .collectLatest { query ->     // cancel the previous query if running and take latest one - Avoid Race Condition
                    if (query.length < 2) {   // search at least 2 chars
                        _uiState.update {
                            it.copy(
                                results = emptyList(),
                                isEmpty = false,
                                isLoading = false
                            )
                        }
                        return@collectLatest
                    }

                    performSearch(query)
                }
        }
    }

    private suspend fun performSearch(query: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val result = repository.searchMovies(query, page = 1)

        if (result.isSuccess) {
            val movies = result.getOrNull() ?: emptyList()
            _uiState.update {
                it.copy(
                    results = movies,
                    isLoading = false,
                    isEmpty = movies.isEmpty()
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    results = emptyList(),
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                        ?: "Something went wrong",
                    isEmpty = false
                )
            }
        }
    }
}