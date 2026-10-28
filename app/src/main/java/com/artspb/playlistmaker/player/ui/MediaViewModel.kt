package com.artspb.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artspb.playlistmaker.medialibrary.domain.db.FavoritesInteractor
import com.artspb.playlistmaker.medialibrary.domain.db.PlaylistsInteractor
import com.artspb.playlistmaker.medialibrary.domain.models.Playlist
import com.artspb.playlistmaker.player.domain.AudioPlayerInteractor
import com.artspb.playlistmaker.player.domain.PlayerState
import com.artspb.playlistmaker.player.ui.models.AddTrackToPlaylistState
import com.artspb.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MediaViewModel(
    track: Track,
    private val audioPlayer: AudioPlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private var currentTrack: Track = track

    private val _playerState = MutableLiveData(PlayerState.DEFAULT)
    val playerState: LiveData<PlayerState> = _playerState

    private val _progressTime = MutableLiveData("00:00")
    val progressTime: LiveData<String> = _progressTime

    private val _trackInfo = MutableLiveData<Track>()
    val trackInfo: LiveData<Track> = _trackInfo

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _playlists = MutableLiveData<List<Playlist>>(emptyList())
    val playlists: LiveData<List<Playlist>> = _playlists

    private val _addTrackState = MutableLiveData<AddTrackToPlaylistState?>()
    val addTrackState: LiveData<AddTrackToPlaylistState?> = _addTrackState

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private var lastCurrentPosition = 0
    private var positionOffset = 0
    
    private var timerJob: Job? = null
    private var playlistsJob: Job? = null

    init {
        _trackInfo.value = currentTrack
        _isFavorite.value = currentTrack.isFavorite
        checkFavoriteStatus()
        preparePlayer()
    }

    private fun checkFavoriteStatus() {
        viewModelScope.launch {
            val isFavorite = favoritesInteractor.isFavoriteTrack(currentTrack.trackId)
            _isFavorite.value = isFavorite
            currentTrack = currentTrack.copy(isFavorite = isFavorite)
        }
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            if (_isFavorite.value == true) {
                favoritesInteractor.deleteTrack(currentTrack)
                _isFavorite.value = false
                currentTrack = currentTrack.copy(isFavorite = false)
            } else {
                favoritesInteractor.addTrack(currentTrack)
                _isFavorite.value = true
                currentTrack = currentTrack.copy(isFavorite = true)
            }
        }
    }

    fun loadPlaylists() {
        playlistsJob?.cancel()
        playlistsJob = viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { playlists ->
                _playlists.postValue(playlists)
            }
        }
    }

    fun onPlaylistClicked(playlist: Playlist) {
        if (playlist.trackIds.contains(currentTrack.trackId)) {
            _addTrackState.value = AddTrackToPlaylistState.AlreadyExists(playlist.name)
        } else {
            viewModelScope.launch {
                playlistsInteractor.addTrackToPlaylist(currentTrack, playlist.playlistId)
                _addTrackState.postValue(AddTrackToPlaylistState.Added(playlist.name))
            }
        }
    }

    fun resetAddTrackState() {
        _addTrackState.value = null
    }

    private fun preparePlayer() {
        val previewUrl = currentTrack.previewUrl
        if (previewUrl.isNullOrEmpty()) return

        audioPlayer.preparePlayer(
            url = previewUrl,
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
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_playerState.value == PlayerState.PLAYING) {
                val currentPosition = audioPlayer.getCurrentPosition()
                if (lastCurrentPosition - currentPosition > 1000) {
                    positionOffset += lastCurrentPosition - currentPosition
                }
                lastCurrentPosition = currentPosition

                val displayTime = currentPosition + positionOffset
                _progressTime.value = dateFormat.format(displayTime)
                delay(UPDATE_TIMER_DELAY)
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
    }

    private fun resetTimer() {
        timerJob?.cancel()
        _progressTime.value = "00:00"
        lastCurrentPosition = 0
        positionOffset = 0
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.releasePlayer()
        timerJob?.cancel()
        playlistsJob?.cancel()
    }

    companion object {
        private const val UPDATE_TIMER_DELAY = 300L
    }
}
