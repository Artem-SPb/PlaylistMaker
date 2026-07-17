package com.artspb.playlistmaker.domain.settings

import com.artspb.playlistmaker.domain.models.ThemeSettings

/**
 * Интерфейс репозитория настроек оформления, который я создал в слое Domain.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Отделяю бизнес-логику управления темой от конкретного хранилища (`SharedPreferences`).
 * 2. Оперирую здесь исключительно чистой доменной моделью `ThemeSettings`.
 */
interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}
