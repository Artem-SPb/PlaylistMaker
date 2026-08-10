package com.artspb.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.artspb.playlistmaker.search.domain.SearchHistoryInteractor
import com.artspb.playlistmaker.search.domain.TracksInteractor
import com.artspb.playlistmaker.search.domain.models.Track
import com.artspb.playlistmaker.search.ui.models.SearchState

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = _state

    private val handler = Handler(Looper.getMainLooper())
    private var lastSearchText: String? = null
    
    private val searchRunnable = Runnable {
        val newSearchText = lastSearchText ?: return@Runnable
        searchRequest(newSearchText)
    }

    init {
        // При инициализации я сразу показываю историю поиска, если она уже есть
        showHistoryOrEmpty()
    }

    fun searchDebounce(changedText: String) {
        if (lastSearchText == changedText) return
        lastSearchText = changedText
        handler.removeCallbacks(searchRunnable)
        if (changedText.isNotEmpty()) {
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        } else {
            showHistoryOrEmpty()
        }
    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            _state.postValue(SearchState.Loading)
            tracksInteractor.searchTracks(newSearchText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    handler.post {
                        if (errorMessage != null) {
                            _state.value = SearchState.Error
                        } else if (foundTracks.isNullOrEmpty()) {
                            _state.value = SearchState.Empty
                        } else {
                            _state.value = SearchState.Content(foundTracks)
                        }
                    }
                }
            })
        }
    }

    fun onFocusChanged(hasFocus: Boolean, text: String) {
        if (hasFocus && text.isEmpty()) {
            showHistoryOrEmpty()
        }
    }

    fun onClearSearchClicked() {
        lastSearchText = ""
        handler.removeCallbacks(searchRunnable)
        showHistoryOrEmpty()
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrack(track)
        // Если сейчас на экране открыта история поиска, я обновляю её после добавления нового трека
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

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(searchRunnable)
    }
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
