package com.artspb.playlistmaker.medialibrary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.artspb.playlistmaker.databinding.FragmentFavoritesBinding
import com.artspb.playlistmaker.medialibrary.ui.models.FavoritesState
import com.artspb.playlistmaker.search.domain.models.Track
import com.artspb.playlistmaker.search.ui.TrackAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Фрагмент для отображения списка избранных треков (слой UI).
 */
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<FavoritesViewModel>()

    private var isClickAllowed = true

    private val trackAdapter by lazy {
        TrackAdapter { track ->
            onTrackClick(track)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.favoritesRecyclerView.adapter = trackAdapter

        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    override fun onResume() {
        super.onResume()
        isClickAllowed = true
        viewModel.fillData()
    }

    private fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Empty -> {
                binding.favoritesRecyclerView.isVisible = false
                binding.placeholderLayout.isVisible = true
            }
            is FavoritesState.Content -> {
                trackAdapter.tracks.clear()
                trackAdapter.tracks.addAll(state.tracks)
                trackAdapter.notifyDataSetChanged()
                binding.placeholderLayout.isVisible = false
                binding.favoritesRecyclerView.isVisible = true
            }
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun onTrackClick(track: Track) {
        if (!clickDebounce()) return

        val action = MedialibraryFragmentDirections.actionMedialibraryFragmentToAudioPlayerFragment(track)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L

        fun newInstance() = FavoritesFragment()
    }
}
