package com.artspb.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.artspb.playlistmaker.R
import com.artspb.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.artspb.playlistmaker.player.domain.PlayerState
import com.artspb.playlistmaker.player.ui.models.AddTrackToPlaylistState
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerFragment : Fragment() {

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    // Получаем аргументы через SafeArgs
    private val args: AudioPlayerFragmentArgs by navArgs()

    private val viewModel by viewModel<MediaViewModel> {
        parametersOf(args.track)
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val playlistAdapter by lazy {
        PlaylistBottomSheetAdapter { playlist ->
            viewModel.onPlaylistClicked(playlist)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        setupBottomSheet()

        viewModel.trackInfo.observe(viewLifecycleOwner) { currentTrack ->
            binding.trackNameTextView.text = currentTrack.trackName
            binding.artistNameTextView.text = currentTrack.artistName
            binding.durationValue.text = dateFormat.format(currentTrack.trackTimeMillis)
            binding.genreValue.text = currentTrack.primaryGenreName
            binding.countryValue.text = currentTrack.country

            if (!currentTrack.collectionName.isNullOrEmpty()) {
                binding.albumValue.text = currentTrack.collectionName
                binding.albumGroup.visibility = View.VISIBLE
            } else {
                binding.albumGroup.visibility = View.GONE
            }

            if (!currentTrack.releaseDate.isNullOrEmpty() && currentTrack.releaseDate.length >= 4) {
                binding.yearValue.text = currentTrack.releaseDate.substring(0, 4)
            } else {
                binding.yearValue.text = ""
            }

            val cornerRadius = resources.getDimensionPixelSize(R.dimen.player_cover_corner_radius)
            Glide.with(this)
                .load(currentTrack.getCoverArtwork())
                .placeholder(R.drawable.ic_placeholder)
                .centerCrop()
                .transform(RoundedCorners(cornerRadius))
                .into(binding.coverImageView)
        }

        binding.playButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        viewModel.playerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                PlayerState.PLAYING -> {
                    binding.playButton.setImageResource(R.drawable.ic_pause_circle)
                    binding.playButton.contentDescription = getString(R.string.player_pause_button_description)
                }
                PlayerState.PAUSED, PlayerState.PREPARED -> {
                    binding.playButton.setImageResource(R.drawable.ic_play_circle)
                    binding.playButton.contentDescription = getString(R.string.player_play_button_description)
                }
                PlayerState.DEFAULT -> {
                    binding.playButton.isEnabled = false
                }
            }
            if (state == PlayerState.PREPARED || state == PlayerState.PLAYING || state == PlayerState.PAUSED) {
                binding.playButton.isEnabled = true
            }
        }

        viewModel.progressTime.observe(viewLifecycleOwner) { timeString ->
            binding.playbackTimeTextView.text = timeString
        }

        binding.favoriteButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            binding.favoriteButton.setImageResource(
                if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            )
        }
    }

    private fun setupBottomSheet() {
        binding.playlistsBottomSheetRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistsBottomSheetRecyclerView.adapter = playlistAdapter

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        _binding?.overlay?.visibility = View.GONE
                    }
                    else -> {
                        _binding?.overlay?.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                _binding?.overlay?.alpha = ((slideOffset + 1f) / 2f).coerceIn(0f, 1f)
            }
        })

        binding.overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.addButton.setOnClickListener {
            viewModel.loadPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.newPlaylistBottomSheetButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val action = AudioPlayerFragmentDirections.actionAudioPlayerFragmentToCreatePlaylistFragment()
            findNavController().navigate(action)
        }

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlistAdapter.playlists.clear()
            playlistAdapter.playlists.addAll(playlists)
            playlistAdapter.notifyDataSetChanged()
        }

        viewModel.addTrackState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AddTrackToPlaylistState.Added -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    com.artspb.playlistmaker.medialibrary.ui.showPlaylistSnackbar(
                        binding.root,
                        getString(R.string.track_added_to_playlist, state.playlistName)
                    )
                    viewModel.resetAddTrackState()
                }
                is AddTrackToPlaylistState.AlreadyExists -> {
                    com.artspb.playlistmaker.medialibrary.ui.showPlaylistSnackbar(
                        binding.root,
                        getString(R.string.track_already_in_playlist, state.playlistName)
                    )
                    viewModel.resetAddTrackState()
                }
                null -> {}
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
