package com.example.moviesdb.ui.movieDetails

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.moviesdb.R
import com.example.moviesdb.databinding.FragmentMovieDetailsBinding
import com.example.moviesdb.ui.uiModels.MovieDetailsModel
import com.example.moviesdb.ui.uiModels.MovieDetailsUiState
import com.example.moviesdb.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val viewModel: MovieDetailsViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movieId = requireArguments().getInt("movieId")
        viewModel.loadMovieDetails(movieId)

        initObserver()
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->

                // Loader
                binding.detailsProgressBar.isVisible = state.isLoading

                // Error
                binding.detailsError.isVisible = state.error != null
                binding.detailsError.text = state.error

                // Data
                binding.scrollViewDetails.isVisible = state.movie != null
                state.movie?.let {
                    bindDetailsToUi(it)
                }
            }
        }
    }

    private fun bindDetailsToUi(movieDetailsModel: MovieDetailsModel) {
        Glide.with(binding.detailsPoster)
            .load(Constants.BASE_IMAGE_URL + movieDetailsModel.posterPath)
            .into(binding.detailsPoster)

        binding.detailsTitle.text = movieDetailsModel.title

        val hours = (movieDetailsModel.runtime ?: 0) / 60
        val minutes = (movieDetailsModel.runtime ?: 0) % 60
        val rating = (movieDetailsModel.voteAverage * 10).roundToInt()

        binding.detailsMeta.text =
            "${movieDetailsModel.releaseDate?.take(4)} • ${hours}h ${minutes}m • $rating% (${movieDetailsModel.voteCount} votes)"

        binding.detailsGenres.text = movieDetailsModel.genres
        binding.detailsOverview.text = movieDetailsModel.overview

        binding.btnShare.setOnClickListener {
            shareMovie(movieDetailsModel)
        }

        binding.detailsBookmark.setImageResource(
            if (movieDetailsModel.isBookmarked)
                R.drawable.ic_bookmark_filled
            else
                R.drawable.ic_bookmark_outline
        )

        binding.detailsBookmark.setOnClickListener {
            viewModel.onBookmarkClick()
        }
    }

    fun shareMovie(movie: MovieDetailsModel) {
        val deepLink ="movieapp://movie/${movie.id}"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                deepLink
            )
        }
        startActivity(Intent.createChooser(intent, getString(R.string.share_via)))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}