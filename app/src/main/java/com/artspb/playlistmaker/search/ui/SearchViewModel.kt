package com.artspb.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artspb.playlistmaker.search.domain.SearchHistoryInteractor
import com.artspb.playlistmaker.search.domain.TracksInteractor
import com.artspb.playlistmaker.search.domain.models.Track
import com.artspb.playlistmaker.search.ui.models.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = _state

    private var lastSearchText: String? = null
    private var searchJob: Job? = null

    init {
        showHistoryOrEmpty()
    }

    fun searchDebounce(changedText: String) {
        if (lastSearchText == changedText) return
        lastSearchText = changedText
        searchJob?.cancel()
        if (changedText.isNotEmpty()) {
            searchJob = viewModelScope.launch {
                delay(SEARCH_DEBOUNCE_DELAY)
                searchRequest(changedText)
            }
        } else {
            showHistoryOrEmpty()
        }
    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            _state.postValue(SearchState.Loading)
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                tracksInteractor.searchTracks(newSearchText).collect { pair ->
                    val foundTracks = pair.first
                    val errorMessage = pair.second
                    
                    if (errorMessage != null) {
                        _state.value = SearchState.Error
                    } else if (foundTracks.isNullOrEmpty()) {
                        _state.value = SearchState.Empty
                    } else {
                        _state.value = SearchState.Content(foundTracks)
                    }
                }
            }
        }
    }

    fun onFocusChanged(hasFocus: Boolean, text: String) {
        if (hasFocus && text.isEmpty()) {
            showHistoryOrEmpty()
        }
    }

    fun onClearSearchClicked() {
        lastSearchText = ""
        searchJob?.cancel()
        showHistoryOrEmpty()
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrack(track)
        if (_state.value is SearchState.History) {
            showHistoryOrEmpty()
        }
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        _state.value = SearchState.History(emptyList())
    }

    private fun showHistoryOrEmpty() {
        val history = searchHistoryInteractor.getHistory()
        if (history.isNotEmpty()) {
            _state.value = SearchState.History(history)
        } else {
            _state.value = SearchState.History(emptyList())
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
