package com.artspb.playlistmaker.data.history

import android.content.SharedPreferences
import com.artspb.playlistmaker.data.dto.TrackDto
import com.artspb.playlistmaker.domain.history.SearchHistoryRepository
import com.artspb.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Моя реализация репозитория истории поиска на базе `SharedPreferences` и `Gson` в слое Data.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Всю работу с JSON (сериализацию/десериализацию через `Gson`) и локальным хранилищем `SharedPreferences`
 *    я полностью изолировал внутри слоя данных.
 * 2. В домен и в UI возвращаются исключительно чистые доменные модели `Track`.
 */
class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {

    override fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<TrackDto>>() {}.type
        val dtos: List<TrackDto> = gson.fromJson(json, type) ?: return emptyList()
        return dtos.map { dto ->
            Track(
                trackId = dto.trackId,
                trackName = dto.trackName,
                artistName = dto.artistName,
                trackTimeMillis = dto.trackTimeMillis,
                artworkUrl100 = dto.artworkUrl100,
                collectionName = dto.collectionName,
                releaseDate = dto.releaseDate,
                primaryGenreName = dto.primaryGenreName,
                country = dto.country,
                previewUrl = dto.previewUrl
            )
        }
    }

    override fun addTrack(track: Track) {
        val currentHistory = getHistory().toMutableList()
        currentHistory.removeAll { it.trackId == track.trackId }
        currentHistory.add(0, track)
        if (currentHistory.size > MAX_HISTORY_SIZE) {
            currentHistory.removeAt(currentHistory.size - 1)
        }
        val dtos = currentHistory.map { t ->
            TrackDto(
                trackId = t.trackId,
                trackName = t.trackName,
                artistName = t.artistName,
                trackTimeMillis = t.trackTimeMillis,
                artworkUrl100 = t.artworkUrl100,
                collectionName = t.collectionName,
                releaseDate = t.releaseDate,
                primaryGenreName = t.primaryGenreName,
                country = t.country,
                previewUrl = t.previewUrl
            )
        }
        val json = gson.toJson(dtos)
        sharedPreferences.edit().putString(HISTORY_KEY, json).apply()
    }

    override fun clearHistory() {
        sharedPreferences.edit().remove(HISTORY_KEY).apply()
    }

    companion object {
        const val HISTORY_KEY = "search_history_key"
        const val MAX_HISTORY_SIZE = 10
    }
}
