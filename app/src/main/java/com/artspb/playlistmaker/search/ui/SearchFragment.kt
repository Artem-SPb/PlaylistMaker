package com.artspb.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.artspb.playlistmaker.R
import com.artspb.playlistmaker.databinding.FragmentSearchBinding
import com.artspb.playlistmaker.search.domain.models.Track
import com.artspb.playlistmaker.search.ui.models.SearchState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel by viewModel<SearchViewModel>()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackAdapter = TrackAdapter { track: Track ->
            onTrackClick(track)
        }
        historyAdapter = TrackAdapter { track: Track ->
            onTrackClick(track)
        }

        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onFocusChanged(hasFocus, binding.inputEditText.text.toString())
        }

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
            viewModel.onClearSearchClicked()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                viewModel.searchDebounce(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.inputEditText.addTextChangedListener(simpleTextWatcher)

        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchRequest(binding.inputEditText.text.toString())
                true
            } else false
        }

        binding.refreshButton.setOnClickListener {
            viewModel.searchRequest(binding.inputEditText.text.toString())
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: SearchState) {
        when (state) {
            is SearchState.Loading -> {
                binding.trackRecyclerView.isVisible = false
                binding.historyHeader.isVisible = false
                binding.clearHistoryButton.isVisible = false
                binding.placeholderContainer.isVisible = false
                binding.progressBarContainer.isVisible = true
            }
            is SearchState.Content -> {
                trackAdapter.tracks.clear()
                trackAdapter.tracks.addAll(state.tracks)
                trackAdapter.notifyDataSetChanged()

                binding.trackRecyclerView.adapter = trackAdapter
                binding.trackRecyclerView.isVisible = true
                binding.historyHeader.isVisible = false
                binding.clearHistoryButton.isVisible = false
                binding.placeholderContainer.isVisible = false
                binding.progressBarContainer.isVisible = false
            }
            is SearchState.Empty -> {
                binding.trackRecyclerView.isVisible = false
                binding.historyHeader.isVisible = false
                binding.clearHistoryButton.isVisible = false
                binding.placeholderContainer.isVisible = true
                binding.progressBarContainer.isVisible = false
                binding.refreshButton.isVisible = false
                binding.placeholderImage.setImageResource(R.drawable.ic_nothing_found)
                binding.placeholderMessage.text = getString(R.string.nothing_found)
            }
            is SearchState.Error -> {
                binding.trackRecyclerView.isVisible = false
                binding.historyHeader.isVisible = false
                binding.clearHistoryButton.isVisible = false
                binding.placeholderContainer.isVisible = true
                binding.progressBarContainer.isVisible = false
                binding.refreshButton.isVisible = true
                binding.placeholderImage.setImageResource(R.drawable.ic_network_error)
                binding.placeholderMessage.text = getString(R.string.network_error)
            }
            is SearchState.History -> {
                historyAdapter.tracks.clear()
                historyAdapter.tracks.addAll(state.tracks)
                historyAdapter.notifyDataSetChanged()

                binding.trackRecyclerView.adapter = historyAdapter
                val isHistoryVisible = state.tracks.isNotEmpty()
                binding.trackRecyclerView.isVisible = isHistoryVisible
                binding.historyHeader.isVisible = isHistoryVisible
                binding.clearHistoryButton.isVisible = isHistoryVisible
                binding.placeholderContainer.isVisible = false
                binding.progressBarContainer.isVisible = false
            }
        }
    }

    private fun onTrackClick(track: Track) {
        if (!clickDebounce()) return

        viewModel.addTrackToHistory(track)

        val action = SearchFragmentDirections.actionSearchFragmentToAudioPlayerFragment(track)
        findNavController().navigate(action)
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
        _binding = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
