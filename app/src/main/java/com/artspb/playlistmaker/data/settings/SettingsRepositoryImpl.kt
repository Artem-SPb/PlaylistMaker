package com.artspb.playlistmaker.data.settings

import android.content.SharedPreferences
import com.artspb.playlistmaker.App
import com.artspb.playlistmaker.domain.models.ThemeSettings
import com.artspb.playlistmaker.domain.settings.SettingsRepository

/**
 * Моя реализация репозитория настроек темы в слое Data.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Инкапсулирую здесь работу с `SharedPreferences` (чтение/запись флага тёмной темы)
 *    и глобальным классом `App` для переключения темы на лету.
 * 2. В слой Domain возвращаю только чистую доменную сущность `ThemeSettings`.
 */
class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val app: App
) : SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        val darkTheme = sharedPreferences.getBoolean(DARK_THEME_KEY, false)
        return ThemeSettings(darkTheme = darkTheme)
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        app.switchTheme(settings.darkTheme)
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_KEY, settings.darkTheme)
            .apply()
    }

    companion object {
        const val DARK_THEME_KEY = "key_for_dark_theme"
    }
}
