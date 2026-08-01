package com.artspb.playlistmaker.sharing.domain

/**
 * Моя реализация интерактора шаринга в слое Domain.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Содержит бизнес-логику для внешних переходов и делегирует их выполнение через `ExternalNavigator`.
 * 2. Гарантирует независимость от Android SDK и конкретных системных `Intent`.
 */
class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink()
    }

    override fun openSupport() {
        externalNavigator.openEmail()
    }

    override fun openTerms() {
        externalNavigator.openTerms()
    }
}
