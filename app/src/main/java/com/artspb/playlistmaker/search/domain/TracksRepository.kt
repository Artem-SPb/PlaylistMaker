package com.artspb.playlistmaker.search.domain

import com.artspb.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс репозитория для поиска треков, который я создал в слое Domain.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Инкапсулирую доступ к данным. Мой слой Domain не знает, откуда именно берутся треки (Retrofit, база или кэш).
 * 2. Теперь я возвращаю Flow (поток данных) с парой: список найденных треков (или `null` при ошибке) и текст ошибки.
 *    Это позволяет мне в интеракторе и в UI асинхронно подписаться на результаты поиска через корутины.
 */
interface TracksRepository {
    fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>>
}
 
