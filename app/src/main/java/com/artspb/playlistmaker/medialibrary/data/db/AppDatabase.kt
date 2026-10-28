package com.artspb.playlistmaker.medialibrary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.artspb.playlistmaker.medialibrary.data.db.dao.PlaylistDao
import com.artspb.playlistmaker.medialibrary.data.db.dao.PlaylistTrackDao
import com.artspb.playlistmaker.medialibrary.data.db.dao.TrackDao
import com.artspb.playlistmaker.medialibrary.data.db.entity.PlaylistEntity
import com.artspb.playlistmaker.medialibrary.data.db.entity.PlaylistTrackEntity
import com.artspb.playlistmaker.medialibrary.data.db.entity.TrackEntity

/**
 * База данных приложения на базе Room (слой Data).
 *
 * Почему я сделал именно так (Clean Architecture):
 * Объединяет таблицы избранных треков, плейлистов и треков в плейлистах, предоставляя DAO-интерфейсы.
 */
@Database(
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao
}
