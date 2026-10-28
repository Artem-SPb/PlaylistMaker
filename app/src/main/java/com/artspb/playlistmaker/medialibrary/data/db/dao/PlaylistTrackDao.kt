package com.artspb.playlistmaker.medialibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.artspb.playlistmaker.medialibrary.data.db.entity.PlaylistTrackEntity

/**
 * DAO интерфейс для работы с таблицей треков, добавленных в плейлисты (слой Data).
 *
 * Почему я сделал именно так:
 * Использую стратегию `OnConflictStrategy.IGNORE`, чтобы при добавлении одного и того же трека
 * в разные плейлисты не дублировать записи в таблице.
 */
@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)
}
