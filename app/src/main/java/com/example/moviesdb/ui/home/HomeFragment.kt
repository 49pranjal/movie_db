package com.example.moviesdb.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviesdb.R
import com.example.moviesdb.databinding.FragmentHomeBinding
import com.example.moviesdb.ui.adapters.MovieAdapter
import com.example.moviesdb.ui.uiModels.MovieModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val viewModel: HomeViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var trendingAdapter: MovieAdapter
    private lateinit var nowPlayingAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialiseRecyclerViews()
        initObserver()
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->

                // Loader (only when no data)
                binding.homeProgressBar.isVisible =
                    state.isLoading && state.error == null &&
                            state.trending.isEmpty() &&
                            state.nowPlaying.isEmpty()

                // Error
                binding.homeError.isVisible =
                    state.error != null &&
                            state.trending.isEmpty() &&
                            state.nowPlaying.isEmpty()

                binding.homeError.text = state.error
                binding.nestedViewHome.isVisible = !(binding.homeProgressBar.isVisible || binding.homeError.isVisible)

                // Data
                trendingAdapter.submitList(state.trending)
                nowPlayingAdapter.submitList(state.nowPlaying)
            }
        }
    }

    private fun initialiseRecyclerViews() {
        trendingAdapter = MovieAdapter(
            ::openDetailsPage,
            ::onBookmarkToggle
            )
        nowPlayingAdapter = MovieAdapter(
            ::openDetailsPage,
            ::onBookmarkToggle)

        binding.rvTrending.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = trendingAdapter

            //For Pagination
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                    val lm = rv.layoutManager as LinearLayoutManager
                    if (!viewModel.isTrendingLoading && lm.findLastVisibleItemPosition() >= lm.itemCount - 3) {
                        viewModel.loadMoreTrending()
                    }
                }
            })
        }

        binding.rvNowPlaying.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = nowPlayingAdapter

            //For Pagination
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                    val lm = rv.layoutManager as LinearLayoutManager
                    if (!viewModel.isNowPlayingLoading && lm.findLastVisibleItemPosition() >= lm.itemCount - 3) {
                        viewModel.loadMoreNowPlaying()
                    }
                }
            })
        }
    }

    private fun openDetailsPage(movieId: Int) {
        findNavController().navigate(R.id.movieDetailsFragment, Bundle().apply {
            putInt("movieId", movieId)
        })
    }

    private fun onBookmarkToggle(movie: MovieModel) {
        viewModel.onBookmarkClick(movie)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}