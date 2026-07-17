package com.artspb.playlistmaker.domain.search

import com.artspb.playlistmaker.domain.models.Track

/**
 * Интерфейс репозитория для поиска треков, который я создал в слое Domain.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Инкапсулирую доступ к данным. Мой слой Domain не знает, откуда именно берутся треки (Retrofit, база или кэш).
 * 2. Возвращаю пару: список найденных треков (или `null` при ошибке) и текст ошибки (или `null` при успехе).
 *    Это позволяет мне в интеракторе и в UI четко различать пустой ответ ("ничего не найдено") и сбой сети.
 */
interface TracksRepository {
    fun searchTracks(expression: String): Pair<List<Track>?, String?>
}
