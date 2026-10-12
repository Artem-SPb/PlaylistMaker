package com.artspb.playlistmaker.medialibrary.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artspb.playlistmaker.medialibrary.domain.db.FavoritesInteractor
import com.artspb.playlistmaker.medialibrary.ui.models.FavoritesState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана избранных треков (слой Presentation).
 *
 * Почему я сделал именно так:
 * Чтобы подписки на бесконечный Flow из Room не накапливались при повторных открытиях экрана (вызовах `fillData()`),
 * я храню `favoritesJob` и отменяю предыдущую перед запуском новой.
 */
class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _state = MutableLiveData<FavoritesState>()
    val state: LiveData<FavoritesState> = _state

    private var favoritesJob: Job? = null

    init {
        fillData()
    }

    fun fillData() {
        favoritesJob?.cancel()
        favoritesJob = viewModelScope.launch {
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
