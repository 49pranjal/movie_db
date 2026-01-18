package com.example.moviesdb.data.repository

import android.content.Context
import com.example.moviesdb.BuildConfig
import com.example.moviesdb.R
import com.example.moviesdb.data.local.db.AppDao
import com.example.moviesdb.data.local.entity.MovieDetailsEntity
import com.example.moviesdb.data.local.entity.MovieEntity
import com.example.moviesdb.data.remote.dtoModels.MovieListResponse
import com.example.moviesdb.data.remote.TMDBApiService
import com.example.moviesdb.ui.uiModels.MovieDetailsModel
import com.example.moviesdb.ui.uiModels.MovieModel
import com.example.moviesdb.utils.Category
import com.example.moviesdb.utils.NetworkUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val appDao: AppDao,
    private val tmdbApiService: TMDBApiService,
    @ApplicationContext private val context: Context
) {

    private val apiKey = BuildConfig.TMDB_API_KEY

    //Home Page Functions

    fun observeTrending(): Flow<List<MovieModel>> =
        appDao.observeMoviesByCategory(Category.TRENDING.value)
            .map { list -> list.map { it.toUiModel() } }

    fun observeNowPlaying(): Flow<List<MovieModel>> =
        appDao.observeMoviesByCategory(Category.NOW_PLAYING.value)
            .map { list -> list.map { it.toUiModel() } }

    fun observeBookmarkedMovies(): Flow<List<MovieModel>> {
        return appDao.observeBookmarkedMovies()
            .map { list -> list.map { it.toUiModel() } }
    }

    suspend fun fetchTrendingFromApi(page: Int): Result<Unit> =
        fetchMoviesAndCache(
            category = Category.TRENDING.value,
            apiCall = { tmdbApiService.getTrending(page, apiKey) }
        )

    suspend fun fetchNowPlayingFromApi(page: Int): Result<Unit> =
        fetchMoviesAndCache(
            category = Category.NOW_PLAYING.value,
            apiCall = { tmdbApiService.getNowPlaying(page, apiKey) }
        )

    private suspend fun fetchMoviesAndCache(
        category: String,
        apiCall: suspend () -> MovieListResponse
    ): Result<Unit> {

        if (!NetworkUtils.isInternetAvailable(context)) {
            return Result.failure(Exception(context.getString(R.string.no_internet_connection)))
        }

        return try {
            //If movies with same Ids come then we can get its bookmark stage
            // and data is not corrupted in db
            val bookmarkedIds = appDao.getBookmarkedIds().toSet()
            val response = apiCall()

            val entities = response.results.map {
                MovieEntity(
                    id = it.id,
                    title = it.title,
                    posterPath = it.poster_path,
                    voteAverage = it.vote_average,
                    releaseDate = it.release_date,
                    category = category,
                    isBookmarked = bookmarkedIds.contains(it.id)
                )
            }

            appDao.insertMovies(entities)
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleBookmark(movieId: Int) {
        appDao.toggleBookmark(movieId)
    }

    //Movie Details Functions
    suspend fun getMovieDetails(movieId: Int): Result<MovieDetailsModel> {

        //If movie detail already present then
        val bookmarkedIds = appDao.getBookmarkedIds().toSet()
        val isBookmarked = bookmarkedIds.contains(movieId)

        if (NetworkUtils.isInternetAvailable(context)) {
            return try {

                val response = tmdbApiService.getMovieDetails(movieId, apiKey)

                val entity = MovieDetailsEntity(
                    id = response.id,
                    title = response.title,
                    posterPath = response.poster_path,
                    overview = response.overview,
                    releaseDate = response.release_date,
                    runtime = response.runtime,
                    voteAverage = response.vote_average,
                    voteCount = response.vote_count,
                    genres = response.genres.joinToString(", ") { it.name }
                )

                appDao.insertMovieDetails(entity)

                Result.success(entity.toUiModel(isBookmarked))

            } catch (e: Exception) {
                fetchMovieDetailsFromDb(movieId, isBookmarked)
            }
        }

        return fetchMovieDetailsFromDb(movieId, isBookmarked)
    }

    private suspend fun fetchMovieDetailsFromDb(movieId: Int, isBookmarked: Boolean): Result<MovieDetailsModel> {
        val cached = appDao.getMovieDetails(movieId)

        return if (cached != null) {
            Result.success(cached.toUiModel(isBookmarked))
        } else {
            Result.failure(Exception(context.getString(R.string.unable_to_load_movie_details_please_check_your_internet_connection)))
        }
    }

    suspend fun toggleBookmarkFromDetails(movieDetails: MovieDetailsModel) {
        val existing = appDao.getMovieByMovieId(movieDetails.id)

        if (existing == null) {
            // Movie NOT present → insert as bookmarked
            val entity = MovieEntity(
                id = movieDetails.id,
                title = movieDetails.title,
                posterPath = movieDetails.posterPath,
                voteAverage = movieDetails.voteAverage,
                releaseDate = movieDetails.releaseDate,
                category = Category.DETAILS.value,
                isBookmarked = true
            )
            appDao.insertMovie(entity)
        } else {
            // Movie exists → toggle bookmark
            appDao.toggleBookmark(movieDetails.id)
        }
    }

    // Search page Function
    suspend fun searchMovies(
        query: String,
        page: Int
    ): Result<List<MovieModel>> {

        if (!NetworkUtils.isInternetAvailable(context)) {
            return Result.failure(Exception(context.getString(R.string.no_internet_connection)))
        }

        return try {

            val response = tmdbApiService.searchMovies(
                query = query,
                page = page,
                apiKey = apiKey
            )

            val movies = response.results.map {
                MovieModel(
                    id = it.id,
                    title = it.title,
                    posterPath = it.poster_path,
                    voteAverage = it.vote_average,
                    releaseDate = it.release_date
                )
            }

            Result.success(movies)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}