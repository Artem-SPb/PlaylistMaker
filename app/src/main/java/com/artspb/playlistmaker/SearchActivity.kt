package com.artspb.playlistmaker

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
// Импортировал для удобной работы с видимостью (совет ревьюера)
import androidx.core.view.isVisible
import androidx.core.view.updatePadding

class SearchActivity : AppCompatActivity() {

    private var searchText: String = SEARCH_DEF
    private lateinit var inputEditText: EditText

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
