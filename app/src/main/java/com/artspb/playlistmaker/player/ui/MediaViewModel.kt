package com.artspb.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.artspb.playlistmaker.creator.Creator
import com.artspb.playlistmaker.player.domain.AudioPlayerInteractor
import com.artspb.playlistmaker.player.domain.PlayerState
import com.artspb.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class MediaViewModel(
    private val track: Track,
    private val audioPlayer: AudioPlayerInteractor
) : ViewModel() {

    private val _playerState = MutableLiveData(PlayerState.DEFAULT)
    val playerState: LiveData<PlayerState> = _playerState

    private val _progressTime = MutableLiveData("00:00")
    val progressTime: LiveData<String> = _progressTime

    private val _trackInfo = MutableLiveData<Track>()
    val trackInfo: LiveData<Track> = _trackInfo

    private val handler = Handler(Looper.getMainLooper())
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private var lastCurrentPosition = 0
    private var positionOffset = 0

    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            if (_playerState.value == PlayerState.PLAYING) {
                val currentPosition = audioPlayer.getCurrentPosition()
                if (lastCurrentPosition - currentPosition > 1000) {
                    positionOffset += lastCurrentPosition - currentPosition
                }
                lastCurrentPosition = currentPosition

                val displayTime = currentPosition + positionOffset
                _progressTime.value = dateFormat.format(displayTime)
                handler.postDelayed(this, UPDATE_TIMER_DELAY)
            }
        }
    }

    init {
        preparePlayer()
    }

    private fun preparePlayer() {
        if (track.previewUrl.isNullOrEmpty()) return

        audioPlayer.preparePlayer(
            url = track.previewUrl,
            onPrepared = {
                _playerState.postValue(PlayerState.PREPARED)
            },
            onCompletion = {
                _playerState.postValue(PlayerState.PREPARED)
                resetTimer()
            },
            onError = {
                _playerState.postValue(PlayerState.DEFAULT)
            }
        )
    }

    private fun startPlayer() {
        audioPlayer.startPlayer()
        _playerState.value = PlayerState.PLAYING
        startTimerUpdate()
    }

    private fun pausePlayer() {
        audioPlayer.pausePlayer()
        _playerState.value = PlayerState.PAUSED
        pauseTimer()
    }

    fun onPlayButtonClicked() {
        when (_playerState.value) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
            else -> {}
        }
    }

    fun onPause() {
        if (_playerState.value == PlayerState.PLAYING) {
            pausePlayer()
        }
    }

    private fun startTimerUpdate() {
        val currentPosition = audioPlayer.getCurrentPosition()
        val displayTime = currentPosition + positionOffset
        _progressTime.value = dateFormat.format(displayTime)
        handler.postDelayed(updateTimerRunnable, UPDATE_TIMER_DELAY)
    }

    private fun pauseTimer() {
        handler.removeCallbacks(updateTimerRunnable)
    }

    private fun resetTimer() {
        handler.removeCallbacks(updateTimerRunnable)
        _progressTime.value = "00:00"
        lastCurrentPosition = 0
        positionOffset = 0
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.releasePlayer()
        handler.removeCallbacks(updateTimerRunnable)
    }

    companion object {
        private const val UPDATE_TIMER_DELAY = 300L

        fun getFactory(track: Track): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MediaViewModel(
                    track = track,
                    audioPlayer = Creator.provideAudioPlayerInteractor()
                )
            }
        }
    }
}
