package com.artspb.playlistmaker

import com.google.gson.annotations.SerializedName

// Обновил модель данных под ответ iTunes API
data class Track(
    @SerializedName("trackName") val trackName: String, // Название композиции
    @SerializedName("artistName") val artistName: String, // Имя исполнителя
    @SerializedName("trackTimeMillis") val trackTimeMillis: Long, // Продолжительность трека (теперь в мс)
    @SerializedName("artworkUrl100") val artworkUrl100: String // Ссылка на обложку
)
