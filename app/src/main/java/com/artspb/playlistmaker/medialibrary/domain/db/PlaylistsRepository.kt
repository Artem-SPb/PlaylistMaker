package com.artspb.playlistmaker.medialibrary.domain.db

import com.artspb.playlistmaker.medialibrary.domain.models.Playlist
import com.artspb.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс репозитория для работы с плейлистами, треками плейлистов и обложками (слой Domain).
 */
interface PlaylistsRepository {
    suspend fun createPlaylist(name: String, description: String, coverImagePath: String?)
    suspend fun updatePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    fun getPlaylistById(playlistId: Long): Flow<Playlist?>
    fun getTracksByIds(trackIds: List<Long>): Flow<List<Track>>
    suspend fun addTrackToPlaylist(track: Track, playlistId: Long)
    suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Long)
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun saveImageToPrivateStorage(uriString: String): String?
}
