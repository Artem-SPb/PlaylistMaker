package com.artspb.playlistmaker.search.domain

import com.artspb.playlistmaker.search.domain.models.Track

/**
 * Моя реализация интерактора истории поиска в слое Domain.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Инкапсулирую бизнес-логику работы с историей прослушиваний.
 * 2. Делегирую операции сохранения репозиторию, сохраняя независимость Domain от Android SDK.
 */
class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}
