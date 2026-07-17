package com.artspb.playlistmaker.presentation.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.artspb.playlistmaker.Creator
import com.artspb.playlistmaker.R
import com.artspb.playlistmaker.domain.history.SearchHistoryInteractor
import com.artspb.playlistmaker.domain.models.Track
import com.artspb.playlistmaker.domain.search.TracksInteractor
import com.artspb.playlistmaker.presentation.player.MediaActivity

class SearchActivity : AppCompatActivity() {

    // Получаем интеракторы из Creator (Clean Architecture)
    private val tracksInteractor: TracksInteractor = Creator.provideTracksInteractor()
    private val searchHistoryInteractor: SearchHistoryInteractor = Creator.provideSearchHistoryInteractor()

    private lateinit var historyHeader: View
    private lateinit var clearHistoryButton: Button

    private lateinit var inputEditText: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var toolbar: Toolbar
    private var searchText: String = SEARCH_DEF

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var trackRecyclerView: RecyclerView

    private lateinit var placeholderContainer: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var refreshButton: Button
    private lateinit var progressBarContainer: View

    private var lastSearchQuery = ""

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchTracks(inputEditText.text.toString()) }

    private var isClickAllowed = true

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        toolbar = findViewById(R.id.toolbar)
        inputEditText = findViewById(R.id.inputEditText)
        clearIcon = findViewById(R.id.clearIcon)
        trackRecyclerView = findViewById(R.id.trackRecyclerView)
        placeholderContainer = findViewById(R.id.placeholderContainer)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        refreshButton = findViewById(R.id.refreshButton)
        progressBarContainer = findViewById(R.id.progressBarContainer)

        historyHeader = findViewById(R.id.historyHeader)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)

        toolbar.setNavigationOnClickListener { finish() }

        trackAdapter = TrackAdapter { track: Track ->
            onTrackClick(track)
        }
        trackRecyclerView.adapter = trackAdapter

        historyAdapter = TrackAdapter { track: Track ->
            onTrackClick(track)
        }
        historyAdapter.tracks = ArrayList(searchHistoryInteractor.getHistory())

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            val isHistoryVisible = hasFocus && inputEditText.text.isEmpty() && searchHistoryInteractor.getHistory().isNotEmpty()
            setHistoryMode(isHistoryVisible)
        }

        clearIcon.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)

            handler.removeCallbacks(searchRunnable)
            trackAdapter.tracks.clear()
            trackAdapter.notifyDataSetChanged()

            if (searchHistoryInteractor.getHistory().isNotEmpty()) {
                setHistoryMode(true)
            } else {
                showPlaceholder(PlaceholderState.SUCCESS)
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s?.toString() ?: ""
                clearIcon.isVisible = !s.isNullOrEmpty()

                val isHistoryVisible = inputEditText.hasFocus() && s?.isEmpty() == true && searchHistoryInteractor.getHistory().isNotEmpty()
                if (isHistoryVisible) {
                    setHistoryMode(true)
                    handler.removeCallbacks(searchRunnable)
                } else if (s?.isEmpty() == true) {
                    setHistoryMode(false)
                    trackAdapter.tracks.clear()
                    trackAdapter.notifyDataSetChanged()
                    showPlaceholder(PlaceholderState.SUCCESS)
                    handler.removeCallbacks(searchRunnable)
                } else {
                    setHistoryMode(false)
                    searchDebounce()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    searchTracks(inputEditText.text.toString())
                }
                true
            } else false
        }

        refreshButton.setOnClickListener {
            searchTracks(lastSearchQuery)
        }

        clearHistoryButton.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            historyAdapter.tracks.clear()
            historyAdapter.notifyDataSetChanged()
            setHistoryMode(false)
        }
    }

    private fun setHistoryMode(isHistoryVisible: Boolean) {
        historyHeader.isVisible = isHistoryVisible
        clearHistoryButton.isVisible = isHistoryVisible

        if (isHistoryVisible) {
            trackRecyclerView.adapter = historyAdapter
            historyAdapter.tracks = ArrayList(searchHistoryInteractor.getHistory())
            historyAdapter.notifyDataSetChanged()
            trackRecyclerView.isVisible = true
            placeholderContainer.isVisible = false
            progressBarContainer.isVisible = false
        } else {
            trackRecyclerView.adapter = trackAdapter
        }
    }

    private fun onTrackClick(track: Track) {
        if (!clickDebounce()) return

        searchHistoryInteractor.addTrack(track)
        historyAdapter.tracks = ArrayList(searchHistoryInteractor.getHistory())
        historyAdapter.notifyDataSetChanged()

        val intent = Intent(this, MediaActivity::class.java).apply {
            putExtra(MediaActivity.EXTRA_TRACK, track)
        }
        startActivity(intent)
    }

    private fun searchTracks(query: String) {
        if (query.isEmpty()) return

        lastSearchQuery = query
        showPlaceholder(PlaceholderState.LOADING)
        setHistoryMode(false)

        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)

        tracksInteractor.searchTracks(query, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                handler.post {
                    if (errorMessage != null) {
                        showPlaceholder(PlaceholderState.ERROR)
                    } else if (foundTracks != null) {
                        trackAdapter.tracks.clear()
                        if (foundTracks.isNotEmpty()) {
                            trackAdapter.tracks.addAll(foundTracks)
                            trackAdapter.notifyDataSetChanged()
                            showPlaceholder(PlaceholderState.SUCCESS)
                        } else {
                            trackAdapter.notifyDataSetChanged()
                            showPlaceholder(PlaceholderState.NOT_FOUND)
                        }
                    }
                }
            }
        })
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun showPlaceholder(state: PlaceholderState) {
        when (state) {
            PlaceholderState.LOADING -> {
                trackRecyclerView.isVisible = false
                historyHeader.isVisible = false
                clearHistoryButton.isVisible = false
                placeholderContainer.isVisible = false
                progressBarContainer.isVisible = true
            }
            PlaceholderState.SUCCESS -> {
                trackRecyclerView.adapter = trackAdapter
                trackRecyclerView.isVisible = true
                historyHeader.isVisible = false
                clearHistoryButton.isVisible = false
                placeholderContainer.isVisible = false
                progressBarContainer.isVisible = false
            }
            PlaceholderState.NOT_FOUND -> {
                trackRecyclerView.isVisible = false
                historyHeader.isVisible = false
                clearHistoryButton.isVisible = false
                placeholderContainer.isVisible = true
                progressBarContainer.isVisible = false
                refreshButton.isVisible = false
                placeholderImage.setImageResource(R.drawable.ic_nothing_found)
                placeholderMessage.text = getString(R.string.nothing_found)
            }
            PlaceholderState.ERROR -> {
                trackRecyclerView.isVisible = false
                historyHeader.isVisible = false
                clearHistoryButton.isVisible = false
                placeholderContainer.isVisible = true
                progressBarContainer.isVisible = false
                refreshButton.isVisible = true
                placeholderImage.setImageResource(R.drawable.ic_network_error)
                placeholderMessage.text = getString(R.string.network_error)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT, SEARCH_DEF) ?: SEARCH_DEF
        inputEditText.setText(searchText)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    enum class PlaceholderState {
        SUCCESS, NOT_FOUND, ERROR, LOADING
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_DEF = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
