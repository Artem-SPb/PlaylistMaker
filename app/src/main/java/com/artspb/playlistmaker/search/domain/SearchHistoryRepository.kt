package com.artspb.playlistmaker.search.domain

import com.artspb.playlistmaker.search.domain.models.Track

/**
 * Интерфейс репозитория для работы с историей поиска в слое Domain.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Скрываю от слоя Domain то, как именно хранится история (`SharedPreferences`, БД или файлы).
 * 2. Предоставляю четкий и понятный контракт для получения, добавления и очистки истории треков.
 */
interface SearchHistoryRepository {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}
