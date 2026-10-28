package com.artspb.playlistmaker.medialibrary.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artspb.playlistmaker.medialibrary.domain.db.PlaylistsInteractor
import com.artspb.playlistmaker.medialibrary.ui.models.PlaylistsState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * ViewModel для вкладки «Плейлисты» на экране Медиатеки (слой Presentation).
 *
 * Почему я сделал именно так:
 * 1. Подписываюсь на Flow со списком плейлистов из Room через интерактор.
 * 2. Храню `playlistsJob` и отменяю предыдущую корутину при повторном вызове `fillData()`,
 *    чтобы не накапливались живые коллекторы.
 */
class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistsState>()
    val state: LiveData<PlaylistsState> = _state

    private var playlistsJob: Job? = null

    init {
        fillData()
    }

    fun fillData() {
        playlistsJob?.cancel()
        playlistsJob = viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { playlists ->
                if (playlists.isEmpty()) {
                    _state.postValue(PlaylistsState.Empty)
                } else {
                    _state.postValue(PlaylistsState.Content(playlists))
                }
            }
        }
    }
}
