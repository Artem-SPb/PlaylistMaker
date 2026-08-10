package com.artspb.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.artspb.playlistmaker.settings.domain.SettingsInteractor
import com.artspb.playlistmaker.settings.domain.models.ThemeSettings
import com.artspb.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {
    private val _themeSettingsState = MutableLiveData<ThemeSettings>()
    val themeSettingsState: LiveData<ThemeSettings> = _themeSettingsState

    init {
        _themeSettingsState.value = settingsInteractor.getThemeSettings()
    }

    fun updateThemeSetting(checked: Boolean) {
        val newTheme = ThemeSettings(checked)
        settingsInteractor.updateThemeSetting(newTheme)
        _themeSettingsState.value = newTheme
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

}
