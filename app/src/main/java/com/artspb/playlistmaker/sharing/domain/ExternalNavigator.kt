package com.artspb.playlistmaker.sharing.domain

/**
 * Интерфейс для взаимодействия с внешними приложениями (окно в слой Data/Android).
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Мой слой Domain не должен знать об Android `Intent`, `Uri`, `Context` или строковых ресурсах (`R.string...`).
 * 2. Все операции по открытию почты, браузера и системного шаринга я абстрагировал этим интерфейсом.
 */
interface ExternalNavigator {
    fun shareLink()
    fun openEmail()
    fun openTerms()
}
