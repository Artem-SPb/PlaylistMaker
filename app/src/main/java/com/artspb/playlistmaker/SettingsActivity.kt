package com.artspb.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Находим кнопку "Назад"
        val backButton = findViewById<ImageView>(R.id.back_button)

        // Вешаем слушатель клика
        backButton.setOnClickListener {
            finish() // Закрываем этот экран и возвращаемся на предыдущий
        }
    }
}
