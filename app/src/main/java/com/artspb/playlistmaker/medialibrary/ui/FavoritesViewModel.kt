package com.artspb.playlistmaker.medialibrary.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artspb.playlistmaker.medialibrary.domain.db.FavoritesInteractor
import com.artspb.playlistmaker.medialibrary.ui.models.FavoritesState
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана избранных треков (слой Presentation).
 */
class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _state = MutableLiveData<FavoritesState>()
    val state: LiveData<FavoritesState> = _state

    init {
        fillData()
    }

    fun fillData() {
        viewModelScope.launch {
            favoritesInteractor.getFavoriteTracks().collect { tracks ->
                if (tracks.isEmpty()) {
                    _state.postValue(FavoritesState.Empty)
                } else {
                    _state.postValue(FavoritesState.Content(tracks))
                }
            }
        }
    }
}
