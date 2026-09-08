package com.artspb.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.artspb.playlistmaker.R
import com.artspb.playlistmaker.player.ui.AudioPlayerFragment
import com.artspb.playlistmaker.search.domain.models.Track
import com.artspb.playlistmaker.search.ui.models.SearchState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment(R.layout.fragment_search) {

    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var historyHeader: View
    private lateinit var clearHistoryButton: Button

    private lateinit var inputEditText: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var toolbar: Toolbar

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var trackRecyclerView: RecyclerView

    private lateinit var placeholderContainer: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var refreshButton: Button
    private lateinit var progressBarContainer: View

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    @SuppressLint("MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Делаем отступ от статус-бара, чтобы элементы не залезли на часы
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.updatePadding(top = statusBar.top)
            insets
        }

        toolbar = view.findViewById(R.id.toolbar)
        inputEditText = view.findViewById(R.id.inputEditText)
        clearIcon = view.findViewById(R.id.clearIcon)
        trackRecyclerView = view.findViewById(R.id.trackRecyclerView)
        placeholderContainer = view.findViewById(R.id.placeholderContainer)
        placeholderImage = view.findViewById(R.id.placeholderImage)
        placeholderMessage = view.findViewById(R.id.placeholderMessage)
        refreshButton = view.findViewById(R.id.refreshButton)
        progressBarContainer = view.findViewById(R.id.progressBarContainer)

        historyHeader = view.findViewById(R.id.historyHeader)
        clearHistoryButton = view.findViewById(R.id.clearHistoryButton)

        trackAdapter = TrackAdapter { track: Track ->
            onTrackClick(track)
        }
        historyAdapter = TrackAdapter { track: Track ->
            onTrackClick(track)
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onFocusChanged(hasFocus, inputEditText.text.toString())
        }

        clearIcon.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            viewModel.onClearSearchClicked()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearIcon.isVisible = !s.isNullOrEmpty()
                viewModel.searchDebounce(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchRequest(inputEditText.text.toString())
                true
            } else false
        }

        refreshButton.setOnClickListener {
            viewModel.searchRequest(inputEditText.text.toString())
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: SearchState) {
        when (state) {
            is SearchState.Loading -> {
                trackRecyclerView.isVisible = false
                historyHeader.isVisible = false
                clearHistoryButton.isVisible = false
                placeholderContainer.isVisible = false
                progressBarContainer.isVisible = true
            }
            is SearchState.Content -> {
                trackAdapter.tracks.clear()
                trackAdapter.tracks.addAll(state.tracks)
                trackAdapter.notifyDataSetChanged()

                trackRecyclerView.adapter = trackAdapter
                trackRecyclerView.isVisible = true
                historyHeader.isVisible = false
                clearHistoryButton.isVisible = false
                placeholderContainer.isVisible = false
                progressBarContainer.isVisible = false
            }
            is SearchState.Empty -> {
                trackRecyclerView.isVisible = false
                historyHeader.isVisible = false
                clearHistoryButton.isVisible = false
                placeholderContainer.isVisible = true
                progressBarContainer.isVisible = false
                refreshButton.isVisible = false
                placeholderImage.setImageResource(R.drawable.ic_nothing_found)
                placeholderMessage.text = getString(R.string.nothing_found)
            }
            is SearchState.Error -> {
                trackRecyclerView.isVisible = false
                historyHeader.isVisible = false
                clearHistoryButton.isVisible = false
                placeholderContainer.isVisible = true
                progressBarContainer.isVisible = false
                refreshButton.isVisible = true
                placeholderImage.setImageResource(R.drawable.ic_network_error)
                placeholderMessage.text = getString(R.string.network_error)
            }
            is SearchState.History -> {
                historyAdapter.tracks.clear()
                historyAdapter.tracks.addAll(state.tracks)
                historyAdapter.notifyDataSetChanged()

                trackRecyclerView.adapter = historyAdapter
                val isHistoryVisible = state.tracks.isNotEmpty()
                trackRecyclerView.isVisible = isHistoryVisible
                historyHeader.isVisible = isHistoryVisible
                clearHistoryButton.isVisible = isHistoryVisible
                placeholderContainer.isVisible = false
                progressBarContainer.isVisible = false
            }
        }
    }

    private fun onTrackClick(track: Track) {
        if (!clickDebounce()) return

        viewModel.addTrackToHistory(track)

        // Переходим на экран плеера с помощью Jetpack Navigation Component
        // и передаем выбранный трек через Bundle
        findNavController().navigate(
            R.id.action_searchFragment_to_audioPlayerFragment,
            bundleOf(AudioPlayerFragment.EXTRA_TRACK to track)
        )
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
