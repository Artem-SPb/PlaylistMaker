package com.artspb.playlistmaker

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    @SerializedName("trackId") val trackId: Long, // ID для проверки уникальности в истории
    @SerializedName("trackName") val trackName: String,
    @SerializedName("artistName") val artistName: String,
    @SerializedName("trackTimeMillis") val trackTimeMillis: Long,
    @SerializedName("artworkUrl100") val artworkUrl100: String,

    // --- Новые поля для экрана Аудиоплеера (Спринт 13) ---

    // Название альбома. Делаем nullable (String?), так как по API оно приходит не всегда
    @SerializedName("collectionName") val collectionName: String?,

    // Дата релиза. Также nullable, так как может отсутствовать
    @SerializedName("releaseDate") val releaseDate: String?,

    // Жанр трека
    @SerializedName("primaryGenreName") val primaryGenreName: String,

    // Страна исполнителя
    @SerializedName("country") val country: String
) : Parcelable {
    /**
     * Функция для получения ссылки на обложку в высоком качестве (512x512).
     * Берем стандартную ссылку artworkUrl100 и заменяем её окончание с помощью
     * встроенной функции Kotlin replaceAfterLast.
     */
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}