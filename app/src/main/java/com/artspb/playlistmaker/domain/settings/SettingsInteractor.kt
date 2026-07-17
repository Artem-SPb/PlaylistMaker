package com.artspb.playlistmaker.domain.settings

import com.artspb.playlistmaker.domain.models.ThemeSettings

/**
 * Интерфейс интерактора настроек (контракт для слоя Presentation).
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Мой UI-слой (`SettingsActivity`) обращается к бизнес-логике настроек только через эту абстракцию.
 * 2. Обеспечиваю единую точку входа для чтения и переключения темы в приложении.
 */
interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}
