package com.artspb.playlistmaker.domain.sharing

/**
 * Интерфейс интерактора для шаринга и связи с поддержкой (контракт для слоя Presentation).
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Отделяю UI (`SettingsActivity`) от механизмов отправки `Intent`.
 * 2. Позволяю UI вызывать понятные бизнес-действия: `shareApp()`, `openSupport()`, `openTerms()`.
 */
interface SharingInteractor {
    fun shareApp()
    fun openSupport()
    fun openTerms()
}
