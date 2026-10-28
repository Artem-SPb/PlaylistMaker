package com.artspb.playlistmaker.player.ui.models

/**
 * Статус попытки добавления трека в плейлист на экране Аудиоплеера (слой Presentation).
 */
sealed interface AddTrackToPlaylistState {
    data class Added(val playlistName: String) : AddTrackToPlaylistState
    data class AlreadyExists(val playlistName: String) : AddTrackToPlaylistState
}
