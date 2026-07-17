package com.artspb.playlistmaker.domain.player

/**
 * Моя реализация интерактора аудиоплеера в слое Domain.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Инкапсулирую здесь бизнес-логику управления аудиопотоком.
 * 2. Взаимодействую со слоем данных исключительно через абстрактный контракт `AudioPlayerControl`.
 */
class AudioPlayerInteractorImpl(
    private val audioPlayerControl: AudioPlayerControl
) : AudioPlayerInteractor {

    override fun preparePlayer(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit,
        onError: () -> Unit
    ) {
        audioPlayerControl.preparePlayer(url, onPrepared, onCompletion, onError)
    }

    override fun startPlayer() {
        audioPlayerControl.startPlayer()
    }

    override fun pausePlayer() {
        audioPlayerControl.pausePlayer()
    }

    override fun releasePlayer() {
        audioPlayerControl.releasePlayer()
    }

    override fun seekTo(msec: Int) {
        audioPlayerControl.seekTo(msec)
    }

    override fun getCurrentPosition(): Int {
        return audioPlayerControl.getCurrentPosition()
    }

    override fun getPlayerState(): PlayerState {
        return audioPlayerControl.getPlayerState()
    }
}
