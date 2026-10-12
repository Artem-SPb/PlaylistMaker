package com.artspb.playlistmaker.medialibrary.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Моя сущность (Entity) таблицы избранных треков для Room (слой Data).
 *
 * Почему я сделал именно так:
 * Содержит полную информацию о треке для отображения в списке
 * и открытия экрана аудиоплеера без дополнительных сетевых запросов.
 */
@Entity(tableName = "favorite_tracks_table")
data class TrackEntity(
    @PrimaryKey
    val trackId: Long,
    val artworkUrl100: String,
    val trackName: String,
    val artistName: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val trackTimeMillis: Long,
    val previewUrl: String?,
    val addedTime: Long = System.currentTimeMillis()
)
