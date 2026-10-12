package com.artspb.playlistmaker.search.data

import android.content.SharedPreferences
import com.artspb.playlistmaker.medialibrary.data.db.AppDatabase
import com.artspb.playlistmaker.search.data.dto.TrackDto
import com.artspb.playlistmaker.search.domain.SearchHistoryRepository
import com.artspb.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/**
 * Моя реализация репозитория истории поиска на базе `SharedPreferences` и `Gson` в слое Data.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Всю работу с JSON (сериализацию/десериализацию через `Gson`) и локальным хранилищем `SharedPreferences`
 *    я полностью изолировал внутри слоя данных.
 * 2. В домен и в UI возвращаются исключительно чистые доменные модели `Track`.
 * 3. При получении треков из истории проверяю их наличие в избранном через Room БД.
 */
class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val appDatabase: AppDatabase
) : SearchHistoryRepository {

    override fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<TrackDto>>() {}.type
        val dtos: List<TrackDto> = gson.fromJson(json, type) ?: return emptyList()
        val favoriteTrackIds = runBlocking(Dispatchers.IO) {
            appDatabase.trackDao().getFavoriteTrackIds()
        }
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
                previewUrl = dto.previewUrl,
                isFavorite = favoriteTrackIds.contains(dto.trackId)
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
