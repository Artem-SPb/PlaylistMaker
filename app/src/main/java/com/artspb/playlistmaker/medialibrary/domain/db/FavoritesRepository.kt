package com.artspb.playlistmaker.medialibrary.domain.db

import com.artspb.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс репозитория для работы с избранными треками (слой Domain).
 */
interface FavoritesRepository {
    suspend fun addTrack(track: Track)
    suspend fun deleteTrack(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun isFavoriteTrack(trackId: Long): Boolean
}
