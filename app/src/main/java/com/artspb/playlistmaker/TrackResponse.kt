package com.artspb.playlistmaker

import com.google.gson.annotations.SerializedName

// Класс-обёртка для ответа сервера iTunes.
// В JSON массивы треков лежат под ключом "results".
class TrackResponse(
    @SerializedName("results") val results: List<Track>
)
