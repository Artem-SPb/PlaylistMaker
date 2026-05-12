package com.artspb.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Интерфейс для Retrofit, описывающий эндпоинты iTunes Search API
interface ItunesApi {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<TrackResponse>
}
