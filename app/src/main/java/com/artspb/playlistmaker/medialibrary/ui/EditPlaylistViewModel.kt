package com.artspb.playlistmaker.medialibrary.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.artspb.playlistmaker.medialibrary.domain.db.PlaylistsInteractor
import com.artspb.playlistmaker.medialibrary.domain.models.Playlist
import com.artspb.playlistmaker.util.SingleLiveData
import kotlinx.coroutines.launch

/**
 * Моя ViewModel-наследник для экрана редактирования существующего плейлиста (слой Presentation).
 *
 * Почему я сделал именно так:
 * 1. Наследуюсь от `CreatePlaylistViewModel`, не дублируя код создания.
 * 2. Передаю исходные данные плейлиста через `initialPlaylist` LiveData для предзаполнения полей экрана.
 * 3. При сохранении изменений копирую новую обложку в приватное хранилище (только если пользователь её поменял),
 *    обновляю запись текущего плейлиста в БД и отправляю одноразовое событие `SingleLiveData` для закрытия экрана.
 */
class EditPlaylistViewModel(
    private val playlist: Playlist,
    playlistsInteractor: PlaylistsInteractor
) : CreatePlaylistViewModel(playlistsInteractor) {

    private val _initialPlaylist = MutableLiveData(playlist)
    val initialPlaylist: LiveData<Playlist> = _initialPlaylist

    private val _playlistUpdated = SingleLiveData<Boolean>()
    val playlistUpdated: LiveData<Boolean> = _playlistUpdated

    fun savePlaylistChanges(name: String, description: String, newCoverUriString: String?) {
        viewModelScope.launch {
            val finalCoverPath = if (!newCoverUriString.isNullOrEmpty()) {
                playlistsInteractor.saveImageToPrivateStorage(newCoverUriString) ?: playlist.coverImagePath
            } else {
                playlist.coverImagePath
            }

            val updatedPlaylist = playlist.copy(
                name = name.trim(),
                description = description.trim(),
                coverImagePath = finalCoverPath
            )
            playlistsInteractor.updatePlaylist(updatedPlaylist)
            _playlistUpdated.postValue(true)
        }
    }
}
