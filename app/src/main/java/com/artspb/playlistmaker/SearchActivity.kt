package com.artspb.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    // Я создаю Mock-объект (тестовый список треков), чтобы настроить и проверить работу RecyclerView
    // до того, как мы начнем получать реальные данные из сети.
    private val trackList: ArrayList<Track> = arrayListOf(
        Track(
            "Smells Like Teen Spirit",
            "Nirvana",
            "5:01",
            "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
        ),
        Track(
            "Billie Jean",
            "Michael Jackson",
            "4:35",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
        ),
        Track(
            "Stayin' Alive",
            "Bee Gees",
            "4:10",
            "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
        ),
        Track(
            "Whole Lotta Love",
            "Led Zeppelin",
            "5:33",
            "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
        ),
        Track(
            "Sweet Child O'Mine",
            "Guns N' Roses",
            "5:03",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
        )
    )

    private var searchText: String = SEARCH_DEF
    private lateinit var inputEditText: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Включаю EdgeToEdge (современный дизайн с 15 Android), чтобы приложение
        // рисовалось на весь экран, залезая под статус-бар и нижнюю панель навигации.
        enableEdgeToEdge()

        setContentView(R.layout.activity_search)

        // 2. Настраиваю отступы, чтобы мой Toolbar не уехал под системные часы и батарею.
        // Ищу корневой элемент (у него id main в xml) и сдвигаю его содержимое вниз на высоту статус-бара.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        // 3. Инициализация UI элементов
        // Вместо кастомной кнопки назад теперь использую стандартный MaterialToolbar
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        inputEditText = findViewById(R.id.inputEditText)
        val clearIcon = findViewById<ImageView>(R.id.clearIcon)

        // Обработка клика "Назад" теперь висит на тулбаре
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Очистка поля и скрытие клавиатуры
        clearIcon.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        // Отслеживаем ввод текста
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Сохраняю текущий текст для onSaveInstanceState
                searchText = s?.toString() ?: ""

                // Управляю крестиком. Ревьюер посоветовал использовать isVisible вместо if/else.
                // Крестик виден (true), если текст НЕ пустой и НЕ null.
                clearIcon.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        // --- ДОБАВЛЯЕМ НАСТРОЙКУ RECYCLERVIEW ---
        // Нахожу RecyclerView в разметке экрана по его ID
        val trackRecyclerView = findViewById<RecyclerView>(R.id.trackRecyclerView)

        // Создаю экземпляр нашего TrackAdapter и передаю в него Mock-список треков.
        val trackAdapter = TrackAdapter(trackList)

        // Связываю RecyclerView с адаптером.
        // Теперь список знает, откуда брать данные и как их отрисовывать на экране.
        trackRecyclerView.adapter = trackAdapter
        // ----------------------------------------

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

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_DEF = ""
    }
}
