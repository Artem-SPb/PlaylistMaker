package com.artspb.playlistmaker.domain.settings

import com.artspb.playlistmaker.domain.models.ThemeSettings

/**
 * Моя реализация интерактора настроек в слое Domain.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Содержит бизнес-логику получения и обновления параметров нашего приложения.
 * 2. Взаимодействует с репозиторием через абстрактный интерфейс `SettingsRepository`.
 */
class SettingsInteractorImpl(
    private val repository: SettingsRepository
) : SettingsInteractor {

    override fun getThemeSettings(): ThemeSettings {
        return repository.getThemeSettings()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        repository.updateThemeSetting(settings)
    }
}
