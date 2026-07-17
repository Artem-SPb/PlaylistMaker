package com.artspb.playlistmaker.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Сетевая DTO-модель трека в слое Data.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Все аннотации Gson (`@SerializedName`) я оставил именно здесь в DTO, чтобы отвязать от них доменную модель `Track`.
 * 2. Если бэкенд вдруг изменит названия полей в JSON, мне достаточно будет поправить только этот класс и маппер,
 *    а вся бизнес-логика и UI останутся чистыми и не потребуют никаких изменений.
 */
data class TrackDto(
    @SerializedName("trackId") val trackId: Long,
    @SerializedName("trackName") val trackName: String,
    @SerializedName("artistName") val artistName: String,
    @SerializedName("trackTimeMillis") val trackTimeMillis: Long,
    @SerializedName("artworkUrl100") val artworkUrl100: String,
    @SerializedName("collectionName") val collectionName: String?,
    @SerializedName("releaseDate") val releaseDate: String?,
    @SerializedName("primaryGenreName") val primaryGenreName: String,
    @SerializedName("country") val country: String,
    @SerializedName("previewUrl") val previewUrl: String?
)
