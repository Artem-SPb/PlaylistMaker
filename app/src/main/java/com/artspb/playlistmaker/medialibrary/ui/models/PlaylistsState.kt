package com.artspb.playlistmaker.medialibrary.ui.models

import com.artspb.playlistmaker.medialibrary.domain.models.Playlist

/**
 * Состояния экрана списка плейлистов в Медиатеке (слой Presentation).
 */
sealed interface PlaylistsState {
    object Empty : PlaylistsState
    data class Content(val playlists: List<Playlist>) : PlaylistsState
}
