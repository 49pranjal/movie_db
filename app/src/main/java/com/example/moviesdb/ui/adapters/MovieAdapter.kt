package com.example.moviesdb.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviesdb.R
import com.example.moviesdb.databinding.ItemMovieBinding
import com.example.moviesdb.ui.uiModels.MovieModel
import com.example.moviesdb.utils.Constants
import kotlin.math.roundToInt

class MovieAdapter (
    private val onMovieClick: (Int) -> Unit,
    private val onBookmarkClick: (MovieModel) -> Unit,
    private val isBookmarkAvailable: Boolean = true

): ListAdapter<MovieModel, MovieAdapter.MovieViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<MovieModel>() {
            override fun areItemsTheSame(old: MovieModel, new: MovieModel) =
                old.id == new.id

            override fun areContentsTheSame(old: MovieModel, new: MovieModel) =
                old == new
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class MovieViewHolder(private val binding: ItemMovieBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieModel) {
            binding.movieTitle.text = movie.title

            // Rating: vote_average * 10 → rounded
            val rating = (movie.voteAverage * 10).roundToInt()
            binding.movieRating.text = "$rating%"

            // Release year
            binding.movieYear.text = movie.releaseDate
                ?.takeIf { it.length >= 4 }
                ?.substring(0, 4)
                ?: ""

            Glide.with(binding.moviePoster)
                .load(Constants.BASE_IMAGE_URL + movie.posterPath)
                .into(binding.moviePoster)

            binding.ivFavoriteMovie.setImageResource(
                if (movie.isBookmarked)
                    R.drawable.ic_bookmark_filled
                else
                    R.drawable.ic_bookmark_outline
            )

            binding.root.setOnClickListener {
                onMovieClick(movie.id)
            }

            binding.ivFavoriteMovie.isVisible = isBookmarkAvailable
            binding.ivFavoriteMovie.setOnClickListener {
                onBookmarkClick(movie)
            }
        }
    }
}