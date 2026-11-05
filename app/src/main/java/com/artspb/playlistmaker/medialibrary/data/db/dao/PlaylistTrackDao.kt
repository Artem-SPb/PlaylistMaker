package com.artspb.playlistmaker.medialibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.artspb.playlistmaker.medialibrary.data.db.entity.PlaylistTrackEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO интерфейс для работы с таблицей треков, добавленных в плейлисты (слой Data).
 *
 * Почему я сделал именно так:
 * 1. Использую стратегию `OnConflictStrategy.IGNORE`, чтобы при добавлении одного и того же трека
 *    в разные плейлисты не дублировать записи в таблице.
 * 2. Добавил получение всех треков и удаление трека по `trackId` для очистки осиротевших треков,
 *    когда они больше не входят ни в один плейлист.
 */
@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_tracks_table")
    fun getAllTracks(): Flow<List<PlaylistTrackEntity>>

    @Query("DELETE FROM playlist_tracks_table WHERE trackId = :trackId")
    suspend fun deleteTrackById(trackId: Long)
}
