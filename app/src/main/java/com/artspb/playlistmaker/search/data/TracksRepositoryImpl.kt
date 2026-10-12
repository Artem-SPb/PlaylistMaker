package com.artspb.playlistmaker.search.data

import com.artspb.playlistmaker.search.data.dto.TracksSearchRequest
import com.artspb.playlistmaker.search.data.dto.TracksSearchResponse
import com.artspb.playlistmaker.search.data.dto.TrackDto
import com.artspb.playlistmaker.search.data.network.NetworkClient
import com.artspb.playlistmaker.search.domain.models.Track
import com.artspb.playlistmaker.search.domain.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Моя реализация репозитория поиска треков в слое Data.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Занимаюсь здесь маппингом (преобразованием) сетевых DTO-моделей `TrackDto` в чистые доменные сущности `Track`.
 * 2. Скрываю от слоя Domain любые детали того, как именно устроен сетевой ответ сервера (`Response.resultCode`).
 * 3. Сетевой запрос обернут в корутинный flow { }, чтобы возвращать данные асинхронным потоком.
 * 4. Проверку на избранное я убрал из поиска по совету ревьюера — теперь актуальный статус проверяется во вьюмодели плеера при открытии трека, что быстрее и надежнее.
 */
class TracksRepositoryImpl(
    private val networkClient: NetworkClient
) : TracksRepository {

    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        if (response.resultCode == 200) {
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
                    previewUrl = dto.previewUrl,
                    isFavorite = false
                )
            }
            emit(Pair(tracks, null))
        } else {
            emit(Pair(null, "Error code: ${response.resultCode}"))
        }
    }
}
 
