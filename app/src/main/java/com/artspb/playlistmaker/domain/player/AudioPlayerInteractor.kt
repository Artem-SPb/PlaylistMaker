package com.artspb.playlistmaker.domain.player

/**
 * Интерфейс интерактора для управления плеером (окно для слоя Presentation).
 *
 * Почему я сделал именно так:
 * 1. Отделяю UI (`MediaActivity`) от прямой работы со слоем данных и контроллером плеера.
 * 2. Поддерживаю единый архитектурный стиль с остальными экранами нашего приложения.
 */
interface AudioPlayerInteractor {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit, onError: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun seekTo(msec: Int)
    fun getCurrentPosition(): Int
    fun getPlayerState(): PlayerState
}
