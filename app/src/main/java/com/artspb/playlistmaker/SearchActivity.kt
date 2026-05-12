package com.artspb.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    // 1. Инициализирую Retrofit и API сервис для поиска в iTunes
    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create()) // Подключаю Gson для парсинга JSON
        .build()
    private val itunesService = retrofit.create(ItunesApi::class.java)

    // Теперь это просто пустой список, который будет заполняться реальными данными из сети
    private val trackList = ArrayList<Track>()
    private lateinit var trackAdapter: TrackAdapter

    private var searchText: String = SEARCH_DEF
    private lateinit var inputEditText: EditText
    private lateinit var trackRecyclerView: RecyclerView

    // UI-элементы для заглушек (плейсхолдеров)
    private lateinit var placeholderContainer: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var refreshButton: Button

    // Переменная для сохранения последнего запроса. Нужна для работы кнопки "Обновить" при ошибке сети.
    private var lastSearchQuery = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Включаю EdgeToEdge, чтобы приложение рисовалось под статус-баром
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        // Настраиваю отступы для корневого элемента, чтобы контент не перекрывался системными иконками
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        // Инициализация всех View элементов
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        inputEditText = findViewById(R.id.inputEditText)
        val clearIcon = findViewById<ImageView>(R.id.clearIcon)
        trackRecyclerView = findViewById(R.id.trackRecyclerView)

        placeholderContainer = findViewById(R.id.placeholderContainer)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        refreshButton = findViewById(R.id.refreshButton)

        // Настройка RecyclerView и адаптера
        trackAdapter = TrackAdapter(trackList)
        trackRecyclerView.adapter = trackAdapter

        // Кнопка "Назад" в тулбаре
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Очистка поля ввода по нажатию на крестик
        clearIcon.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)

            // По ТЗ: при нажатии на крестик мы также должны очистить список треков и скрыть плейсхолдеры
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            showPlaceholder(PlaceholderState.SUCCESS) // Успешное (пустое) состояние без заглушек
        }

        // Отслеживаем ввод текста
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s?.toString() ?: ""
                clearIcon.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        // --- НОВОЕ: Обработка кнопки "Done" (Enter) на клавиатуре ---
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    searchTracks(inputEditText.text.toString())
                }
                true
            } else {
                false
            }
        }

        // --- НОВОЕ: Обработка кнопки "Обновить" при ошибке сети ---
        refreshButton.setOnClickListener {
            searchTracks(lastSearchQuery)
        }
    }

    // Метод для выполнения сетевого запроса к iTunes
    private fun searchTracks(query: String) {
        lastSearchQuery = query // Сохраняем запрос на случай, если придется его повторить

        // При запуске поиска прячем клавиатуру (улучшаем UX)
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)

        itunesService.search(query).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.code() == 200) {
                    trackList.clear()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        // Успех: треки найдены
                        trackList.addAll(response.body()?.results!!)
                        trackAdapter.notifyDataSetChanged()
                        showPlaceholder(PlaceholderState.SUCCESS)
                    } else {
                        // Успех: сервер ответил, но ничего не найдено по этому запросу
                        trackAdapter.notifyDataSetChanged()
                        showPlaceholder(PlaceholderState.NOT_FOUND)
                    }
                } else {
                    // Сервер вернул ошибку (код отличный от 200)
                    showPlaceholder(PlaceholderState.ERROR)
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                // Ошибка сети (интернет отключен, таймаут)
                showPlaceholder(PlaceholderState.ERROR)
            }
        })
    }

    // Централизованное управление видимостью элементов экрана (Best Practice)
    // Я использую Enum, чтобы избежать путаницы с множеством if/else для visibility
    private fun showPlaceholder(state: PlaceholderState) {
        when (state) {
            PlaceholderState.SUCCESS -> {
                trackRecyclerView.isVisible = true
                placeholderContainer.isVisible = false
            }
            PlaceholderState.NOT_FOUND -> {
                trackRecyclerView.isVisible = false
                placeholderContainer.isVisible = true
                refreshButton.isVisible = false // Кнопка "Обновить" здесь не нужна

                placeholderImage.setImageResource(R.drawable.ic_nothing_found)
                placeholderMessage.text = getString(R.string.nothing_found)
            }
            PlaceholderState.ERROR -> {
                trackRecyclerView.isVisible = false
                placeholderContainer.isVisible = true
                refreshButton.isVisible = true // Показываем кнопку "Обновить"

                placeholderImage.setImageResource(R.drawable.ic_network_error)
                placeholderMessage.text = getString(R.string.network_error)
            }
        }
    }

    // Сохраняю текст при повороте экрана
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    // Восстанавливаю текст после поворота экрана
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT, SEARCH_DEF)
        inputEditText.setText(searchText)
    }

    // Enum-класс для возможных состояний экрана
    enum class PlaceholderState {
        SUCCESS, NOT_FOUND, ERROR
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_DEF = ""
    }
}

