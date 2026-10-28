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
 * 2. В `addTrackToPlaylist` принимаю `playlistId: Long` (по совету ревьюера, чтобы не гонять тяжелые объекты между слоями).
 *    Получаю актуальную запись плейлиста по ID, сохраняю трек в `playlist_tracks_table` и обновляю список ID треков и счётчик.
 * 3. При сохранении обложки в приватное хранилище использую `runCatching` и проверку результата `decodeStream` на `null`,
 *    чтобы при поврежденном или неподдерживаемом файле приложение не падало с NPE и не оставляло пустой файл.
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

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long) {
        val playlistEntity = playlistDao.getPlaylistById(playlistId) ?: return
        val playlist = playlistDbConvertor.map(playlistEntity)

        playlistTrackDao.insertTrack(playlistDbConvertor.mapToPlaylistTrackEntity(track))
        val updatedTrackIds = playlist.trackIds + track.trackId
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            tracksCount = updatedTrackIds.size
        )
        playlistDao.updatePlaylist(playlistDbConvertor.map(updatedPlaylist))
    }

    override suspend fun saveImageToPrivateStorage(uriString: String): String? = withContext(Dispatchers.IO) {
        runCatching {
            val uri = Uri.parse(uriString)
            val bitmap = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            } ?: return@withContext null

            val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_covers")
            if (!filePath.exists()) {
                filePath.mkdirs()
            }
            val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
            }
            file.absolutePath
        }.getOrNull()
    }
}
