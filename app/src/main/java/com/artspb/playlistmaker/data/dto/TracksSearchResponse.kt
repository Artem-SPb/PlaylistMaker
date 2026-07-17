package com.artspb.playlistmaker.data.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO-ответ от сервера iTunes с массивом найденных треков в слое Data.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Наследуюсь от базового класса `Response`, чтобы получить поле `resultCode` для проверок статуса.
 * 2. Оборачиваю список `TrackDto`, который приходит от iTunes в JSON-поле `"results"`.
 */
class TracksSearchResponse(
    @SerializedName("results") val results: List<TrackDto>
) : Response()
