package com.example.moviesdb.data.repository

import com.example.moviesdb.data.local.entity.MovieDetailsEntity
import com.example.moviesdb.data.local.entity.MovieEntity
import com.example.moviesdb.ui.uiModels.MovieDetailsModel
import com.example.moviesdb.ui.uiModels.MovieModel

fun MovieEntity.toUiModel() = MovieModel(
    id = id,
    title = title,
    posterPath = posterPath,
    voteAverage = voteAverage,
    releaseDate = releaseDate,
    isBookmarked = isBookmarked
)

fun MovieDetailsEntity.toUiModel(isBookmarked: Boolean) = MovieDetailsModel(
    id = id,
    title = title,
    posterPath = posterPath,
    overview = overview,
    releaseDate = releaseDate,
    runtime = runtime,
    voteAverage = voteAverage,
    voteCount = voteCount,
    genres = genres,
    isBookmarked = isBookmarked
)

