package com.artspb.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Включаем EdgeToEdge для главного экрана
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        // 2. Делаем отступ от статус-бара, чтобы контент не уехал под системные часы.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        val searchButton = findViewById<Button>(R.id.search_button)
        val mediaButton = findViewById<Button>(R.id.media_button)
        val settingsButton = findViewById<Button>(R.id.settings_button)

        // =========================================================
        // СПОСОБ 1: Через анонимный класс (Переход на экран Поиска)
        // =========================================================
        val searchButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Создаем Intent для перехода на SearchActivity
                val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(searchIntent)
            }
        }
        searchButton.setOnClickListener(searchButtonClickListener)

        // =========================================================
        // СПОСОБ 2: Через лямбду (Переход на экран Медиатеки)
        // =========================================================
        mediaButton.setOnClickListener {
            // Создаем Intent для перехода на MediaActivity
            val mediaIntent = Intent(this, MediaActivity::class.java)
            startActivity(mediaIntent)
        }

        // =========================================================
        // Переход на экран Настроек (через лямбду)
        // =========================================================
        settingsButton.setOnClickListener {
            // Создаем Intent для перехода на SettingsActivity
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}
