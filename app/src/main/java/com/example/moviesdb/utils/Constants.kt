package com.example.moviesdb.utils

object Constants {
    const val BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500"
    const val BASE_URL = "https://api.themoviedb.org/3/"
}

enum class Category(val value: String) {
    TRENDING("TRENDING"),
    NOW_PLAYING("NOW_PLAYING"),
    DETAILS("DETAILS")
}