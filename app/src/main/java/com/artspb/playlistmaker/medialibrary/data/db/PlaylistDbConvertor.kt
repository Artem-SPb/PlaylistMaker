package com.artspb.playlistmaker.medialibrary.data.db

import com.artspb.playlistmaker.medialibrary.data.db.entity.PlaylistEntity
import com.artspb.playlistmaker.medialibrary.data.db.entity.PlaylistTrackEntity
import com.artspb.playlistmaker.medialibrary.domain.models.Playlist
import com.artspb.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Мой конвертер для преобразования между доменной моделью `Playlist` и сущностями БД (слой Data).
 *
 * Почему я сделал именно так (Clean Architecture):
 * Вся работа с сериализацией списка `trackIds` в JSON через `Gson` изолирована здесь в слое Data,
 * чтобы слой Domain работал с обычным списком `List<Long>`.
 */
class PlaylistDbConvertor(
    private val gson: Gson
) {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            name = playlist.name,
            description = playlist.description,
            coverImagePath = playlist.coverImagePath,
            trackIdsJson = gson.toJson(playlist.trackIds),
            tracksCount = playlist.tracksCount
        )
    }

    fun map(entity: PlaylistEntity): Playlist {
        val type = object : TypeToken<List<Long>>() {}.type
        val trackIds: List<Long> = if (entity.trackIdsJson.isNotEmpty()) {
            gson.fromJson(entity.trackIdsJson, type) ?: emptyList()
        } else {
            emptyList()
        }
        return Playlist(
            playlistId = entity.playlistId,
            name = entity.name,
            description = entity.description,
            coverImagePath = entity.coverImagePath,
            trackIds = trackIds,
            tracksCount = entity.tracksCount
        )
    }

    fun mapToPlaylistTrackEntity(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTimeMillis = track.trackTimeMillis,
            previewUrl = track.previewUrl
        )
    }
}
