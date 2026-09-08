package com.artspb.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.artspb.playlistmaker.R
import com.artspb.playlistmaker.player.domain.PlayerState
import com.artspb.playlistmaker.search.domain.models.Track
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerFragment : Fragment(R.layout.fragment_audio_player) {

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private lateinit var playButton: ImageButton
    private lateinit var playbackTimeTextView: TextView

    private val viewModel by viewModel<MediaViewModel> {
        // Достаем трек из аргументов фрагмента, переданных через Navigation Component
        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(EXTRA_TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getParcelable<Track>(EXTRA_TRACK)
        }
        parametersOf(track)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(EXTRA_TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable<Track>(EXTRA_TRACK)
        }

        if (track == null) {
            findNavController().navigateUp()
            return
        }

        val backButton = view.findViewById<ImageButton>(R.id.backButton)
        val coverImageView = view.findViewById<ImageView>(R.id.coverImageView)
        val trackName = view.findViewById<TextView>(R.id.trackNameTextView)
        val artistName = view.findViewById<TextView>(R.id.artistNameTextView)
        val durationValue = view.findViewById<TextView>(R.id.durationValue)
        val albumGroup = view.findViewById<Group>(R.id.albumGroup)
        val albumValue = view.findViewById<TextView>(R.id.albumValue)
        val yearValue = view.findViewById<TextView>(R.id.yearValue)
        val genreValue = view.findViewById<TextView>(R.id.genreValue)
        val countryValue = view.findViewById<TextView>(R.id.countryValue)

        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.trackInfo.observe(viewLifecycleOwner) { currentTrack ->
            trackName.text = currentTrack.trackName
            artistName.text = currentTrack.artistName
            durationValue.text = dateFormat.format(currentTrack.trackTimeMillis)
            genreValue.text = currentTrack.primaryGenreName
            countryValue.text = currentTrack.country

            if (!currentTrack.collectionName.isNullOrEmpty()) {
                albumValue.text = currentTrack.collectionName
                albumGroup.visibility = View.VISIBLE
            } else {
                albumGroup.visibility = View.GONE
            }

            if (!currentTrack.releaseDate.isNullOrEmpty() && currentTrack.releaseDate.length >= 4) {
                yearValue.text = currentTrack.releaseDate.substring(0, 4)
            } else {
                yearValue.text = ""
            }

            val cornerRadius = resources.getDimensionPixelSize(R.dimen.player_cover_corner_radius)
            Glide.with(this)
                .load(currentTrack.getCoverArtwork())
                .placeholder(R.drawable.ic_placeholder)
                .centerCrop()
                .transform(RoundedCorners(cornerRadius))
                .into(coverImageView)
        }

        playButton = view.findViewById(R.id.playButton)
        playbackTimeTextView = view.findViewById(R.id.playbackTimeTextView)

        playButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        viewModel.playerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                PlayerState.PLAYING -> {
                    playButton.setImageResource(R.drawable.ic_pause_circle)
                    playButton.contentDescription = getString(R.string.player_pause_button_description)
                }
                PlayerState.PAUSED, PlayerState.PREPARED -> {
                    playButton.setImageResource(R.drawable.ic_play_circle)
                    playButton.contentDescription = getString(R.string.player_play_button_description)
                }
                PlayerState.DEFAULT -> {
                    playButton.isEnabled = false
                }
            }
            if (state == PlayerState.PREPARED || state == PlayerState.PLAYING || state == PlayerState.PAUSED) {
                playButton.isEnabled = true
            }
        }

        viewModel.progressTime.observe(viewLifecycleOwner) { timeString ->
            playbackTimeTextView.text = timeString
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    companion object {
        const val EXTRA_TRACK = "extra_track"
    }
}
