package com.artspb.playlistmaker.medialibrary.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artspb.playlistmaker.medialibrary.domain.db.PlaylistsInteractor
import com.artspb.playlistmaker.medialibrary.domain.models.Playlist
import com.artspb.playlistmaker.search.domain.models.Track
import com.artspb.playlistmaker.sharing.domain.SharingInteractor
import com.artspb.playlistmaker.util.SingleLiveData
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Моя ViewModel для экрана просмотра информации о плейлисте «Плейлист» (слой Presentation).
 *
 * Почему я сделал именно так:
 * 1. Подписываюсь на плейлист по его `playlistId` через Flow, а при каждом обновлении плейлиста
 *    получаю актуальный список его треков через `getTracksByIds`, поэтому любые изменения
 *    (удаление трека или редактирование информации) сразу реактивно обновляют UI и длительность.
 * 2. Для одноразового события удаления плейлиста использую `SingleLiveData`, чтобы оно не срабатывало повторно при повороте экрана.
 * 3. Для отправки списка треков в другие приложения использую `SharingInteractor`.
 */
class PlaylistDetailsViewModel(
    private val playlistId: Long,
    private val playlistsInteractor: PlaylistsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val _playlist = MutableLiveData<Playlist?>()
    val playlist: LiveData<Playlist?> = _playlist

    private val _tracks = MutableLiveData<List<Track>>(emptyList())
    val tracks: LiveData<List<Track>> = _tracks

    private val _playlistDeleted = SingleLiveData<Boolean>()
    val playlistDeleted: LiveData<Boolean> = _playlistDeleted

    private var tracksJob: Job? = null

    init {
        observePlaylist()
    }

    private fun observePlaylist() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylistById(playlistId).collect { currentPlaylist ->
                _playlist.postValue(currentPlaylist)
                if (currentPlaylist != null) {
                    loadTracks(currentPlaylist.trackIds)
                }
            }
        }
    }

    private fun loadTracks(trackIds: List<Long>) {
        tracksJob?.cancel()
        if (trackIds.isEmpty()) {
            _tracks.postValue(emptyList())
            return
        }
        tracksJob = viewModelScope.launch {
            playlistsInteractor.getTracksByIds(trackIds).collect { loadedTracks ->
                _tracks.postValue(loadedTracks)
            }
        }
    }

    fun deleteTrack(track: Track) {
        viewModelScope.launch {
            playlistsInteractor.removeTrackFromPlaylist(track.trackId, playlistId)
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistsInteractor.deletePlaylist(playlistId)
            _playlistDeleted.postValue(true)
        }
    }

    fun shareText(message: String) {
        sharingInteractor.shareText(message)
    }

    override fun onCleared() {
        super.onCleared()
        tracksJob?.cancel()
    }
}
