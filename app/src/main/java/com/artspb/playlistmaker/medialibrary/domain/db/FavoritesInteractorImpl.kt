package com.artspb.playlistmaker.medialibrary.domain.db

import com.artspb.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

/**
 * Моя реализация интерактора для управления избранными треками (слой Domain).
 *
 * Почему я сделал именно так (Clean Architecture):
 * Инкапсулирует бизнес-логику работы с избранным и связывает Presentation слой с репозиторием.
 */
class FavoritesInteractorImpl(
    private val favoritesRepository: FavoritesRepository
) : FavoritesInteractor {

    override suspend fun addTrack(track: Track) {
        favoritesRepository.addTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        favoritesRepository.deleteTrack(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoritesRepository.getFavoriteTracks()
    }

    override suspend fun isFavoriteTrack(trackId: Long): Boolean {
        return favoritesRepository.isFavoriteTrack(trackId)
    }
}
