package com.artspb.playlistmaker.search.ui.models

import com.artspb.playlistmaker.search.domain.models.Track

sealed interface SearchState {
    object Loading : SearchState
    data class Content(val tracks: List<Track>) : SearchState
    object Error : SearchState
    object Empty : SearchState
    data class History(val tracks: List<Track>) : SearchState
}
