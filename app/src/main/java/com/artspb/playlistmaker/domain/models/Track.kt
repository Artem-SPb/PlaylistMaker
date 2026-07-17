package com.artspb.playlistmaker.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Моя чистая доменная модель трека (Бизнес-модель в слое Domain).
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. В этой модели нет никаких сетевых аннотаций (`@SerializedName`), чтобы слой Domain был полностью
 *    независим от внешних библиотек (Gson/Retrofit) и того, как данные приходят по сети.
 * 2. Для сетевого обмена я создал в слое Data отдельный `TrackDto`, который при получении мапится сюда.
 */
@Parcelize
data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String?
) : Parcelable {
    /**
     * Функция для получения ссылки на обложку в высоком качестве (512x512).
     */
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}
