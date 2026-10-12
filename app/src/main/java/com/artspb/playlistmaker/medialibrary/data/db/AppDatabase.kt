package com.artspb.playlistmaker.medialibrary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.artspb.playlistmaker.medialibrary.data.db.dao.TrackDao
import com.artspb.playlistmaker.medialibrary.data.db.entity.TrackEntity

/**
 * База данных приложения на базе Room (слой Data).
 *
 * Почему я сделал именно так (Clean Architecture):
 * Создаю базу с единой точкой доступа к таблице избранного через абстрактный `trackDao()`.
 */
@Database(
    entities = [TrackEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
}
