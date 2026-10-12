package com.artspb.playlistmaker.medialibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.artspb.playlistmaker.medialibrary.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO интерфейс для работы с таблицей избранных треков в Room.
 */
@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Delete
    suspend fun deleteTrack(track: TrackEntity)

    @Query("SELECT * FROM favorite_tracks_table ORDER BY addedTime DESC")
    fun getFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM favorite_tracks_table")
    suspend fun getFavoriteTrackIds(): List<Long>
}
