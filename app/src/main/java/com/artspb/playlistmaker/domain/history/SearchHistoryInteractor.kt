package com.artspb.playlistmaker.domain.history

import com.artspb.playlistmaker.domain.models.Track

/**
 * Интерфейс интерактора для управления историей поиска (контракт для слоя Presentation).
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Мой UI (`SearchActivity`) общается с историей исключительно через этот интерактор, не зная про репозиторий.
 * 2. Соблюдаю принцип единственной ответственности (SRP) для операций с историей.
 */
interface SearchHistoryInteractor {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}
