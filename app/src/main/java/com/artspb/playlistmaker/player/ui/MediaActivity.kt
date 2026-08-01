package com.artspb.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.ViewModelProvider
import com.artspb.playlistmaker.R
import com.artspb.playlistmaker.search.domain.models.Track
import com.artspb.playlistmaker.player.domain.PlayerState
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class MediaActivity : AppCompatActivity() {

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private lateinit var playButton: ImageButton
    private lateinit var playbackTimeTextView: TextView
    private lateinit var viewModel: MediaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Track>(EXTRA_TRACK)
        }

        if (track == null) {
            finish()
            return
        }

        viewModel = ViewModelProvider(this, MediaViewModel.getFactory(track))
            .get(MediaViewModel::class.java)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        val coverImageView = findViewById<ImageView>(R.id.coverImageView)
        val trackName = findViewById<TextView>(R.id.trackNameTextView)
        val artistName = findViewById<TextView>(R.id.artistNameTextView)
        val durationValue = findViewById<TextView>(R.id.durationValue)
        val albumGroup = findViewById<Group>(R.id.albumGroup)
        val albumValue = findViewById<TextView>(R.id.albumValue)
        val yearValue = findViewById<TextView>(R.id.yearValue)
        val genreValue = findViewById<TextView>(R.id.genreValue)
        val countryValue = findViewById<TextView>(R.id.countryValue)

        backButton.setOnClickListener {
            finish()
        }

        viewModel.trackInfo.observe(this) { currentTrack ->
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

        playButton = findViewById(R.id.playButton)
        playbackTimeTextView = findViewById(R.id.playbackTimeTextView)

        playButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        viewModel.playerState.observe(this) { state ->
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

        viewModel.progressTime.observe(this) { timeString ->
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
