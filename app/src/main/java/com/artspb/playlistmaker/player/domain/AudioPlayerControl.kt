package com.artspb.playlistmaker.player.domain

/**
 * Контракт для низкоуровневого управления аудиоплеером в слое Domain (окно в слой Data).
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Выношу работу с `MediaPlayer` за этот абстрактный интерфейс, чтобы в будущем при необходимости
 *    легко заменить аудиодвижок (например, на ExoPlayer), не меняя ни строчки в Domain или UI.
 * 2. Соблюдаю принцип инверсии зависимостей (DIP).
 */
interface AudioPlayerControl {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit, onError: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun seekTo(msec: Int)
    fun getCurrentPosition(): Int
    fun getPlayerState(): PlayerState
}
