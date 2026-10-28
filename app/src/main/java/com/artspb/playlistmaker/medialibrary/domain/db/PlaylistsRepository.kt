package com.artspb.playlistmaker.medialibrary.domain.db

import com.artspb.playlistmaker.medialibrary.domain.models.Playlist
import com.artspb.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс репозитория для работы с плейлистами и обложками (слой Domain).
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. В слое Domain мы не используем Android-специфичные классы (`Uri`), поэтому путь к выбранному
 *    изображению передается в виде строки `String`, а вся работа с `ContentResolver` и файловой
 *    системой скрыта внутри реализации репозитория в слое Data.
 * 2. В `addTrackToPlaylist` передаю только `playlistId: Long`, чтобы не гонять тяжелые объекты между слоями.
 */
interface PlaylistsRepository {
    suspend fun createPlaylist(name: String, description: String, coverImagePath: String?)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(track: Track, playlistId: Long)
    suspend fun saveImageToPrivateStorage(uriString: String): String?
}
