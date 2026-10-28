package com.artspb.playlistmaker.medialibrary.domain.db

import com.artspb.playlistmaker.medialibrary.domain.models.Playlist
import com.artspb.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс репозитория для работы с плейлистами и обложками (слой Domain).
 *
 * Почему я сделал именно так (Clean Architecture):
 * В слое Domain мы не используем Android-специфичные классы (`Uri`), поэтому путь к выбранному
 * изображению передается в виде строки `String`, а вся работа с `ContentResolver` и файловой
 * системой скрыта внутри реализации репозитория в слое Data.
 */
interface PlaylistsRepository {
    suspend fun createPlaylist(name: String, description: String, coverImagePath: String?)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)
    suspend fun saveImageToPrivateStorage(uriString: String): String
}
