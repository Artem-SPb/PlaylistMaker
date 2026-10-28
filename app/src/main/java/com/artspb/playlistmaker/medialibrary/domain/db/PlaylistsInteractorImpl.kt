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

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getPlaylists()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long) {
        playlistsRepository.addTrackToPlaylist(track, playlistId)
    }

    override suspend fun saveImageToPrivateStorage(uriString: String): String? {
        return playlistsRepository.saveImageToPrivateStorage(uriString)
    }
}
