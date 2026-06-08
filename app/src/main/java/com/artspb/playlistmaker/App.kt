package com.artspb.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

// Выносим константы на уровень файла, чтобы использовать их в SettingsActivity
const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
const val DARK_THEME_KEY = "key_for_dark_theme"

class App : Application() {
    // Здесь я храню текущее состояние темы, чтобы другие экраны могли к нему обратиться
    var darkTheme = false
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        // Инициализируем SharedPreferences при старте приложения
        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        // Читаем сохраненную тему (по умолчанию false - светлая)
        darkTheme = sharedPrefs.getBoolean(DARK_THEME_KEY, false)
        // Применяем тему сразу при запуске
        switchTheme(darkTheme)
    }

    // Метод для переключения темы (использую AppCompatDelegate)
    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}