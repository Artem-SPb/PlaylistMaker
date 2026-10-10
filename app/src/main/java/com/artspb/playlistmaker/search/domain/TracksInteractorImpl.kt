package com.artspb.playlistmaker.search.domain

import com.artspb.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

/**
 * Моя реализация бизнес-логики поиска треков в слое Domain.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Теперь я не управляю потоками вручную через `Executors`, а просто прокидываю `Flow` из репозитория.
 * 2. Благодаря этому я полностью отвязал UI от ручного управления потоками, делегировав это корутинам.
 */
class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expression)
    }
}
