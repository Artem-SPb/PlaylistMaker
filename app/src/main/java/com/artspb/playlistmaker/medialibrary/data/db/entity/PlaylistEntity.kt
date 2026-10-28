package com.artspb.playlistmaker.medialibrary.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Моя сущность (Entity) таблицы плейлистов для Room (слой Data).
 *
 * Почему я сделал именно так:
 * 1. Поле `playlistId` генерируется автоматически с помощью `@PrimaryKey(autoGenerate = true)`.
 * 2. Список идентификаторов треков `trackIdsJson` хранится в виде JSON-строки (преобразуется через Gson в конвертере).
 */
@Entity(tableName = "playlists_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long = 0,
    val name: String,
    val description: String,
    val coverImagePath: String?,
    val trackIdsJson: String,
    val tracksCount: Int
)
