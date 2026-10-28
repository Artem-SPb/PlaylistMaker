package com.artspb.playlistmaker.medialibrary.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Моя чистая доменная модель плейлиста (слой Domain).
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Модель полностью независима от аннотаций Room и способов хранения в БД.
 * 2. Все поля сделаны неизменяемыми (`val`), а для обновления (например, при добавлении трека)
 *    я использую подход copy-on-write.
 */
@Parcelize
data class Playlist(
    val playlistId: Long = 0,
    val name: String,
    val description: String,
    val coverImagePath: String?,
    val trackIds: List<Long> = emptyList(),
    val tracksCount: Int = 0
) : Parcelable
