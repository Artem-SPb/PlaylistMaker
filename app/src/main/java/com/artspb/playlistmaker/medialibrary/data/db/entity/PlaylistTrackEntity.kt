package com.artspb.playlistmaker.medialibrary.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Моя сущность (Entity) для таблицы треков, добавленных хотя бы в один плейлист (слой Data).
 *
 * Почему я сделал именно так:
 * Хранит полную информацию о треке отдельно от таблицы избранного, чтобы при удалении трека
 * из избранного он не пропадал из созданных пользователем плейлистов.
 */
@Entity(tableName = "playlist_tracks_table")
data class PlaylistTrackEntity(
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
