package com.artspb.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

import com.artspb.playlistmaker.settings.domain.SettingsInteractor
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import com.artspb.playlistmaker.di.dataModule
import com.artspb.playlistmaker.di.interactorModule
import com.artspb.playlistmaker.di.repositoryModule
import com.artspb.playlistmaker.di.viewModelModule

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"

/**
 * Главный класс приложения. Инициализирует Koin и применяет сохраненную тему.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }

        val settingsInteractor: SettingsInteractor = getKoin().get()
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