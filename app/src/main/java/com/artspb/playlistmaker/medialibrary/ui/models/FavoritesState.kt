package com.artspb.playlistmaker.medialibrary.ui.models

import com.artspb.playlistmaker.search.domain.models.Track

/**
 * Состояния экрана избранных треков (слой Presentation).
 */
sealed interface FavoritesState {
    object Empty : FavoritesState
    data class Content(val tracks: List<Track>) : FavoritesState
}
