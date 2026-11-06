package com.artspb.playlistmaker.sharing.domain

/**
 * Интерфейс интерактора для шаринга и связи с поддержкой (контракт для слоя Presentation).
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Отделяю UI от механизмов отправки `Intent`.
 * 2. Позволяю UI вызывать понятные бизнес-действия: `shareApp()`, `shareText(...)`, `openSupport()`, `openTerms()`.
 */
interface SharingInteractor {
    fun shareApp()
    fun shareText(text: String)
    fun openSupport()
    fun openTerms()
}
