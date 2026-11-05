package com.artspb.playlistmaker.medialibrary.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artspb.playlistmaker.medialibrary.domain.db.PlaylistsInteractor
import kotlinx.coroutines.launch

/**
 * Моя базовая ViewModel для экрана создания нового плейлиста (слой Presentation).
 *
 * Почему я сделал её `open`:
 * Чтобы `EditPlaylistViewModel` могла наследоваться от неё и переиспользовать зависимость `playlistsInteractor`,
 * как рекомендовано в подсказках задания.
 */
open class CreatePlaylistViewModel(
    protected val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _playlistCreated = MutableLiveData<String?>()
    val playlistCreated: LiveData<String?> = _playlistCreated

    fun createPlaylist(name: String, description: String, coverUriString: String?) {
        viewModelScope.launch {
            val savedCoverPath = if (!coverUriString.isNullOrEmpty()) {
                playlistsInteractor.saveImageToPrivateStorage(coverUriString)
            } else {
                null
            }
            playlistsInteractor.createPlaylist(
                name = name.trim(),
                description = description.trim(),
                coverImagePath = savedCoverPath
            )
            _playlistCreated.postValue(name.trim())
        }
    }
}
