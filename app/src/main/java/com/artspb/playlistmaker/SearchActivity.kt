package com.artspb.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    // Инициализирую Retrofit и API
    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ItunesApi::class.java)

    // Переменные для истории поиска
    private lateinit var searchHistory: SearchHistory
    private lateinit var historyLayout: View
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button

    // Теперь у нас ДВА адаптера: один для результатов, другой для истории
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private var searchText: String = SEARCH_DEF
    private lateinit var inputEditText: EditText
    private lateinit var trackRecyclerView: RecyclerView

    // UI-элементы для заглушек
    private lateinit var placeholderContainer: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var refreshButton: Button

    private var lastSearchQuery = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        // Инициализация SharedPreferences и менеджера истории
        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs, Gson())

        // Инициализация View
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        inputEditText = findViewById(R.id.inputEditText)
        val clearIcon = findViewById<ImageView>(R.id.clearIcon)
        trackRecyclerView = findViewById(R.id.trackRecyclerView)

        placeholderContainer = findViewById(R.id.placeholderContainer)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        refreshButton = findViewById(R.id.refreshButton)

        historyLayout = findViewById(R.id.historyLayout)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)

        toolbar.setNavigationOnClickListener { finish() }

        // --- НАСТРОЙКА АДАПТЕРОВ ---

        // 1. Адаптер для результатов поиска
        trackAdapter = TrackAdapter { track: Track ->
            onTrackClick(track)
        }
        trackRecyclerView.adapter = trackAdapter

        // 2. Адаптер для истории поиска
        historyAdapter = TrackAdapter { track: Track ->
            onTrackClick(track)
        }
        historyRecyclerView.adapter = historyAdapter
        // При старте экрана сразу подтягиваем историю из памяти
        historyAdapter.tracks = searchHistory.getHistory()

        // --- ЛОГИКА ФОКУСА ---
        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            val isHistoryVisible = hasFocus && inputEditText.text.isEmpty() && searchHistory.getHistory().isNotEmpty()
            historyLayout.isVisible = isHistoryVisible

            // Если показываем историю, скрываем результаты
            if (isHistoryVisible) {
                trackRecyclerView.isVisible = false
                placeholderContainer.isVisible = false
            }
        }

        // Очистка поля ввода по нажатию на крестик
        clearIcon.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)

            // Очищаем результаты поиска
            trackAdapter.tracks.clear()
            trackAdapter.notifyDataSetChanged()
            showPlaceholder(PlaceholderState.SUCCESS)

            // Если история не пуста, после очистки поля она должна появиться
            if (searchHistory.getHistory().isNotEmpty()) {
                historyLayout.isVisible = true
                trackRecyclerView.isVisible = false
            }
        }

        // Отслеживаем ввод текста
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s?.toString() ?: ""
                clearIcon.isVisible = !s.isNullOrEmpty()

                // Прячем или показываем историю на лету при изменении текста
                val isHistoryVisible = inputEditText.hasFocus() && s?.isEmpty() == true && searchHistory.getHistory().isNotEmpty()
                historyLayout.isVisible = isHistoryVisible

                if (isHistoryVisible) {
                    trackRecyclerView.isVisible = false
                    placeholderContainer.isVisible = false
                } else if (s?.isEmpty() == true) {
                    trackAdapter.tracks.clear()
                    trackAdapter.notifyDataSetChanged()
                    showPlaceholder(PlaceholderState.SUCCESS)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        // Обработка кнопки "Done" (Enter)
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    searchTracks(inputEditText.text.toString())
                }
                true
            } else false
        }

        // Обработка кнопки "Обновить"
        refreshButton.setOnClickListener {
            searchTracks(lastSearchQuery)
        }

        // Обработка кнопки "Очистить историю"
        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            historyAdapter.tracks.clear()
            historyAdapter.notifyDataSetChanged()
            historyLayout.isVisible = false
        }
    }

    /**
     * Единый метод обработки клика по треку (Спринт 13).
     * Выносим сюда логику, чтобы не дублировать код для trackAdapter и historyAdapter.
     */
    private fun onTrackClick(track: Track) {
        // 1. Добавляем трек в историю (он поднимется наверх)
        searchHistory.addTrack(track)

        // 2. Обновляем адаптер истории
        historyAdapter.tracks = searchHistory.getHistory()
        historyAdapter.notifyDataSetChanged()

        // 3. Создаем Intent для перехода на экран плеера (MediaActivity)
        val intent = Intent(this, MediaActivity::class.java).apply {
            // Превращаем объект Track в JSON-строку и кладем в Intent
            val trackJson = Gson().toJson(track)
            putExtra(MediaActivity.EXTRA_TRACK, trackJson)
        }

        // Запускаем экран плеера
        startActivity(intent)
    }

    private fun searchTracks(query: String) {
        lastSearchQuery = query

        // При начале поиска обязательно скрываем историю
        historyLayout.isVisible = false

        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)

        itunesService.search(query).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.isSuccessful) {
                    trackAdapter.tracks.clear()
                    val results = response.body()?.results
                    if (!results.isNullOrEmpty()) {
                        trackAdapter.tracks.addAll(results)
                        trackAdapter.notifyDataSetChanged()
                        showPlaceholder(PlaceholderState.SUCCESS)
                    } else {
                        trackAdapter.notifyDataSetChanged()
                        showPlaceholder(PlaceholderState.NOT_FOUND)
                    }
                } else {
                    showPlaceholder(PlaceholderState.ERROR)
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showPlaceholder(PlaceholderState.ERROR)
            }
        })
    }

    private fun showPlaceholder(state: PlaceholderState) {
        when (state) {
            PlaceholderState.SUCCESS -> {
                trackRecyclerView.isVisible = true
                placeholderContainer.isVisible = false
            }
            PlaceholderState.NOT_FOUND -> {
                trackRecyclerView.isVisible = false
                placeholderContainer.isVisible = true
                refreshButton.isVisible = false
                placeholderImage.setImageResource(R.drawable.ic_nothing_found)
                placeholderMessage.text = getString(R.string.nothing_found)
            }
            PlaceholderState.ERROR -> {
                trackRecyclerView.isVisible = false
                placeholderContainer.isVisible = true
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
        searchText = savedInstanceState.getString(SEARCH_TEXT, SEARCH_DEF)
        inputEditText.setText(searchText)
    }

    enum class PlaceholderState {
        SUCCESS, NOT_FOUND, ERROR
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_DEF = ""
    }
}
