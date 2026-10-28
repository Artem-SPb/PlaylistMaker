package com.artspb.playlistmaker.medialibrary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.artspb.playlistmaker.databinding.FragmentPlaylistsBinding
import com.artspb.playlistmaker.medialibrary.ui.models.PlaylistsState
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Фрагмент вкладки «Плейлисты» на экране Медиатеки (слой UI).
 *
 * Почему я сделал именно так:
 * 1. Использую `GridLayoutManager` с 2 колонками для отображения карточек плейлистов.
 * 2. При нажатии на кнопку «Новый плейлист» перехожу на `CreatePlaylistFragment` через Navigation Component.
 * 3. В `onResume()` обновляю данные, чтобы после возвращения с экрана создания плейлиста список всегда был актуальным.
 */
class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlaylistsViewModel>()

    private val playlistAdapter by lazy {
        PlaylistGridAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playlistsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsRecyclerView.adapter = playlistAdapter

        binding.newPlaylistButton.setOnClickListener {
            val action = MedialibraryFragmentDirections.actionMedialibraryFragmentToCreatePlaylistFragment()
            findNavController().navigate(action)
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fillData()
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> {
                binding.playlistsRecyclerView.isVisible = false
                binding.placeholderLayout.isVisible = true
            }
            is PlaylistsState.Content -> {
                playlistAdapter.playlists.clear()
                playlistAdapter.playlists.addAll(state.playlists)
                playlistAdapter.notifyDataSetChanged()
                binding.placeholderLayout.isVisible = false
                binding.playlistsRecyclerView.isVisible = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}
