package com.artspb.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    // Переменная для хранения текста поискового запроса. По умолчанию она пустая.
    private var searchText: String = SEARCH_DEF

    // Выносим EditText в свойства класса (lateinit), чтобы иметь к нему доступ
    // не только в onCreate, но и в методе onRestoreInstanceState.
    private lateinit var inputEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        inputEditText = findViewById(R.id.inputEditText)
        val clearIcon = findViewById<ImageView>(R.id.clearIcon)

        // Закрываем экран при нажатии на стрелку "Назад"
        btnBack.setOnClickListener {
            finish()
        }

        // Очищаем поле и прячем клавиатуру при нажатии на крестик
        clearIcon.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        // Слежу за тем, что вводит пользователь
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ничего не делаю
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 1. Сохраняем текущий текст в нашу переменную (согласно заданию)
                // Если s null, запишется пустая строка (благодаря оператору ?: "")
                searchText = s?.toString() ?: ""

                // 2. Управляем видимостью крестика
                if (s.isNullOrEmpty()) {
                    clearIcon.visibility = View.GONE
                } else {
                    clearIcon.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Ничего не делаю
            }
        }

        // Подключаю слушатель к полю ввода
        inputEditText.addTextChangedListener(simpleTextWatcher)
    }

    // Метод вызывается ПЕРЕД тем, как Activity будет уничтожена (например, при перевороте экрана)
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Кладем нашу строку в "чемодан" Bundle под уникальным ключом
        outState.putString(SEARCH_TEXT, searchText)
    }

    // Метод вызывается ПОСЛЕ того, как Activity была пересоздана, если в "чемодане" что-то есть
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Достаем строку из Bundle. Если по ключу ничего нет, используем значение по умолчанию (SEARCH_DEF)
        searchText = savedInstanceState.getString(SEARCH_TEXT, SEARCH_DEF)

        // Устанавливаем восстановленный текст обратно в поле ввода
        inputEditText.setText(searchText)
    }

    // companion object - это аналог статических переменных в Java.
    // Здесь мы храним константы, чтобы не хардкодить ключи в коде.
    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_DEF = ""
    }
}
