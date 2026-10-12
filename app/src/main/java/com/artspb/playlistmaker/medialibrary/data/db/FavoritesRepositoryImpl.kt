package com.artspb.playlistmaker.medialibrary.data.db

import com.artspb.playlistmaker.medialibrary.data.db.dao.TrackDao
import com.artspb.playlistmaker.medialibrary.domain.db.FavoritesRepository
import com.artspb.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Моя реализация репозитория для работы с избранными треками через Room (слой Data).
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Работаю напрямую через DAO-интерфейс `TrackDao`, а не через всю БД, что делает класс легче и упрощает unit-тестирование.
 * 2. Метод getFavoriteTracks возвращает Flow, поэтому UI автоматически реагирует на любые изменения в БД.
 */
class FavoritesRepositoryImpl(
    private val trackDao: TrackDao,
    private val trackDbConvertor: TrackDbConvertor
) : FavoritesRepository {

    override suspend fun addTrack(track: Track) {
        val entity = trackDbConvertor.map(track)
        trackDao.insertTrack(entity)
    }

    override suspend fun deleteTrack(track: Track) {
        val entity = trackDbConvertor.map(track)
        trackDao.deleteTrack(entity)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return trackDao.getFavoriteTracks().map { entities ->
            entities.map { trackDbConvertor.map(it) }
        }
    }

    override suspend fun isFavoriteTrack(trackId: Long): Boolean {
        return trackDao.getFavoriteTrackIds().contains(trackId)
    }
}
