package com.artspb.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.artspb.playlistmaker.creator.Creator
import com.artspb.playlistmaker.settings.domain.SettingsInteractor

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"

/**
 * Главный класс приложения. Инициализирует Creator и применяет сохраненную тему.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Creator.init(this)

        val settingsInteractor: SettingsInteractor = Creator.provideSettingsInteractor()
        val themeSettings = settingsInteractor.getThemeSettings()
        switchTheme(themeSettings.darkTheme)
    }

    /**
     * Переключение дневной/ночной темы приложения.
     */
    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}