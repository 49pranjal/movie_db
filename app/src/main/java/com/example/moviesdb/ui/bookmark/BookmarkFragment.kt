package com.example.moviesdb.ui.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.moviesdb.R
import com.example.moviesdb.databinding.FragmentBoomarkBinding
import com.example.moviesdb.ui.adapters.GridSpacingItemDecoration
import com.example.moviesdb.ui.adapters.MovieAdapter
import com.example.moviesdb.ui.uiModels.MovieModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarkFragment : Fragment() {

    private var _binding: FragmentBoomarkBinding? = null
    private val viewModel: BookmarkViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var bookmarAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoomarkBinding.inflate(inflater, container, false)
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
                bookmarAdapter.submitList(state.movies)
                binding.bookmarkEmpty.isVisible = state.isEmpty
            }
        }
    }

    private fun initialiseRecyclerViews() {
        bookmarAdapter = MovieAdapter(
            ::openDetailsPage,
            ::onUnbookmark
        )

        binding.rvBookmark.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(GridSpacingItemDecoration(16))
            adapter = bookmarAdapter
        }
    }

    private fun openDetailsPage(movieId: Int) {
        findNavController().navigate(R.id.movieDetailsFragment, Bundle().apply {
            putInt("movieId", movieId)
        })
    }

    private fun onUnbookmark(movie: MovieModel) {
        viewModel.onUnbookmark(movie)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}