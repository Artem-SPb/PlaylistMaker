package com.artspb.playlistmaker.search.data

import com.artspb.playlistmaker.search.data.dto.TracksSearchRequest
import com.artspb.playlistmaker.search.data.dto.TracksSearchResponse
import com.artspb.playlistmaker.search.data.network.NetworkClient
import com.artspb.playlistmaker.search.domain.models.Track
import com.artspb.playlistmaker.search.domain.TracksRepository

/**
 * Моя реализация репозитория поиска треков в слое Data.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Занимаюсь здесь маппингом (преобразованием) сетевых DTO-моделей `TrackDto` в чистые доменные сущности `Track`.
 * 2. Скрываю от слоя Domain любые детали того, как именно устроен сетевой ответ сервера (`Response.resultCode`).
 */
class TracksRepositoryImpl(
    private val networkClient: NetworkClient
) : TracksRepository {

    override fun searchTracks(expression: String): Pair<List<Track>?, String?> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return if (response.resultCode == 200) {
            val tracksSearchResponse = response as TracksSearchResponse
            val tracks = tracksSearchResponse.results.map { dto ->
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
            Pair(tracks, null)
        } else {
            Pair(null, "Ошибка сети: ${response.resultCode}")
        }
    }
}
