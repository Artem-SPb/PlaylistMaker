package com.artspb.playlistmaker.search.data.network

import com.artspb.playlistmaker.search.data.dto.TracksSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Интерфейс для Retrofit, где я описываю эндпоинты iTunes Search API (слой Data).
 * Перенес его в пакет `data.network` и переключил на работу через DTO `TracksSearchResponse`.
 */
interface ItunesApi {
    @GET("/search?entity=song")
    suspend fun search(@Query("term") text: String): TracksSearchResponse
}
