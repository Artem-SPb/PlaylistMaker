package com.artspb.playlistmaker.medialibrary.domain.db

import com.artspb.playlistmaker.medialibrary.domain.models.Playlist
import com.artspb.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс интерактора для управления плейлистами (слой Domain).
 */
interface PlaylistsInteractor {
    suspend fun createPlaylist(name: String, description: String, coverImagePath: String?)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(track: Track, playlistId: Long)
    suspend fun saveImageToPrivateStorage(uriString: String): String?
}
