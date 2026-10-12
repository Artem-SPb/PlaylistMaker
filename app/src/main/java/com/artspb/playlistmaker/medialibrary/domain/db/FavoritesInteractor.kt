package com.artspb.playlistmaker.medialibrary.domain.db

import com.artspb.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс интерактора для управления избранными треками (слой Domain).
 */
interface FavoritesInteractor {
    suspend fun addTrack(track: Track)
    suspend fun deleteTrack(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun isFavoriteTrack(trackId: Long): Boolean
}
