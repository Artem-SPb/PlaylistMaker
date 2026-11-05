package com.artspb.playlistmaker.medialibrary.domain.db

import com.artspb.playlistmaker.medialibrary.domain.models.Playlist
import com.artspb.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

/**
 * Моя реализация интерактора для работы с плейлистами (слой Domain).
 */
class PlaylistsInteractorImpl(
    private val playlistsRepository: PlaylistsRepository
) : PlaylistsInteractor {

    override suspend fun createPlaylist(name: String, description: String, coverImagePath: String?) {
        playlistsRepository.createPlaylist(name, description, coverImagePath)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistsRepository.updatePlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getPlaylists()
    }

    override fun getPlaylistById(playlistId: Long): Flow<Playlist?> {
        return playlistsRepository.getPlaylistById(playlistId)
    }

    override fun getTracksByIds(trackIds: List<Long>): Flow<List<Track>> {
        return playlistsRepository.getTracksByIds(trackIds)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long) {
        playlistsRepository.addTrackToPlaylist(track, playlistId)
    }

    override suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Long) {
        playlistsRepository.removeTrackFromPlaylist(trackId, playlistId)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistsRepository.deletePlaylist(playlistId)
    }

    override suspend fun saveImageToPrivateStorage(uriString: String): String? {
        return playlistsRepository.saveImageToPrivateStorage(uriString)
    }
}
