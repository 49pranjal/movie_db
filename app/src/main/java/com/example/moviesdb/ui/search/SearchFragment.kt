package com.example.moviesdb.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.moviesdb.R
import com.example.moviesdb.databinding.FragmentSearchBinding
import com.example.moviesdb.ui.adapters.GridSpacingItemDecoration
import com.example.moviesdb.ui.adapters.MovieAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val viewModel: SearchViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var searchedAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialiseRecyclerViews()
        initObserver()
        setUpSearchBtn()
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->

                //Loading
                binding.searchProgressBar.isVisible = state.isLoading

                //Error
                binding.searchError.isVisible = state.error != null

                //No items found
                binding.searchEmpty.isVisible = state.isEmpty

                //Data
                binding.searchError.text = state.error
                searchedAdapter.submitList(state.results)
            }
        }
    }

    private fun setUpSearchBtn() {
        binding.etSearch.addTextChangedListener {
            viewModel.onQueryChanged(it.toString())
        }
    }

    private fun initialiseRecyclerViews() {
        searchedAdapter = MovieAdapter(
            onMovieClick = ::openDetailsPage,
            onBookmarkClick = {},
            isBookmarkAvailable = false
        )

        binding.rvSearch.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(GridSpacingItemDecoration(2,16))
            adapter = searchedAdapter
        }
    }

    private fun openDetailsPage(movieId: Int) {
        findNavController().navigate(R.id.movieDetailsFragment, Bundle().apply {
            putInt("movieId", movieId)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}