package com.artspb.playlistmaker.medialibrary.data.db

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.artspb.playlistmaker.medialibrary.data.db.dao.PlaylistDao
import com.artspb.playlistmaker.medialibrary.data.db.dao.PlaylistTrackDao
import com.artspb.playlistmaker.medialibrary.domain.db.PlaylistsRepository
import com.artspb.playlistmaker.medialibrary.domain.models.Playlist
import com.artspb.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Моя реализация репозитория для работы с плейлистами и локальным хранилищем обложек (слой Data).
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Передаю сюда напрямую `PlaylistDao` и `PlaylistTrackDao`, а не всю базу данных.
 * 2. При добавлении трека в плейлист сначала сохраняю сам трек в таблицу `playlist_tracks_table`,
 *    а затем обновляю список ID треков и счётчик в самом плейлисте через copy-on-write.
 * 3. Копирую файл выбранной обложки во внутреннее хранилище приложения (`getExternalFilesDir`),
 *    чтобы картинка оставалась доступной даже после удаления оригинала пользователем.
 */
class PlaylistsRepositoryImpl(
    private val context: Context,
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val playlistDbConvertor: PlaylistDbConvertor
) : PlaylistsRepository {

    override suspend fun createPlaylist(name: String, description: String, coverImagePath: String?) {
        val playlist = Playlist(
            name = name,
            description = description,
            coverImagePath = coverImagePath,
            trackIds = emptyList(),
            tracksCount = 0
        )
        playlistDao.insertPlaylist(playlistDbConvertor.map(playlist))
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getPlaylists().map { entities ->
            entities.map { playlistDbConvertor.map(it) }
        }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        playlistTrackDao.insertTrack(playlistDbConvertor.mapToPlaylistTrackEntity(track))
        val updatedTrackIds = playlist.trackIds + track.trackId
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            tracksCount = updatedTrackIds.size
        )
        playlistDao.updatePlaylist(playlistDbConvertor.map(updatedPlaylist))
    }

    override suspend fun saveImageToPrivateStorage(uriString: String): String = withContext(Dispatchers.IO) {
        val uri = Uri.parse(uriString)
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_covers")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                BitmapFactory.decodeStream(inputStream)
                    .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
            }
        }
        file.absolutePath
    }
}
