package com.artspb.playlistmaker.medialibrary.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artspb.playlistmaker.medialibrary.domain.db.PlaylistsInteractor
import kotlinx.coroutines.launch

/**
 * Моя ViewModel для экрана создания нового плейлиста (слой Presentation).
 *
 * Почему я сделал именно так:
 * 1. При нажатии на кнопку «Создать» сначала сохраняю обложку во внутреннее хранилище приложения (если она была выбрана),
 *    а затем создаю запись о плейлисте в базе данных Room через интерактор.
 * 2. После завершения сохранения отправляю событие через LiveData `playlistCreated`, чтобы фрагмент показал Toast и закрыл экран.
 */
class CreatePlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
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
