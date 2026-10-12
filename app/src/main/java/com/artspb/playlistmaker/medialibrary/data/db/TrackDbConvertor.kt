package com.artspb.playlistmaker.medialibrary.data.db

import com.artspb.playlistmaker.medialibrary.data.db.entity.TrackEntity
import com.artspb.playlistmaker.search.domain.models.Track

/**
 * Мой конвертер между доменной моделью Track и сущностью БД TrackEntity (слой Data).
 *
 * Почему я сделал именно так (Clean Architecture):
 * Слой Domain не должен знать про Room-аннотации (@Entity, @PrimaryKey),
 * поэтому я преобразую данные между слоями в этом конвертере.
 */
class TrackDbConvertor {

    fun map(track: Track): TrackEntity {
        return TrackEntity(
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

    fun map(entity: TrackEntity): Track {
        return Track(
            trackId = entity.trackId,
            trackName = entity.trackName,
            artistName = entity.artistName,
            trackTimeMillis = entity.trackTimeMillis,
            artworkUrl100 = entity.artworkUrl100,
            collectionName = entity.collectionName,
            releaseDate = entity.releaseDate,
            primaryGenreName = entity.primaryGenreName,
            country = entity.country,
            previewUrl = entity.previewUrl,
            isFavorite = true
        )
    }
}
