package com.artspb.playlistmaker

import android.media.MediaPlayer

/**
 * Реализация интерфейса AudioPlayerControl на базе стандартного Android MediaPlayer.
 * Инкапсулирует в себе всю низкоуровневую работу с аудиопотоком.
 *
 * Почему это Best Practices:
 * 1. Вся специфика работы с MediaPlayer (установка слушателей, асинхронная подготовка, отлов исключений)
 *    скрыта внутри одного класса. UI-слой (MediaActivity) больше не перегружен деталями реализации плеера.
 * 2. Обеспечивается строгое соответствие перечислению PlayerState при любом событии плеера.
 */
class AndroidAudioPlayerImpl : AudioPlayerControl {

    private var mediaPlayer = MediaPlayer()
    private var playerState = PlayerState.DEFAULT

    override fun preparePlayer(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit,
        onError: () -> Unit
    ) {
        if (url.isEmpty()) {
            onError()
            return
        }
        try {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playerState = PlayerState.PREPARED
                onPrepared()
            }
            mediaPlayer.setOnCompletionListener {
                playerState = PlayerState.PREPARED
                onCompletion()
            }
            mediaPlayer.setOnErrorListener { _, _, _ ->
                onError()
                true
            }
        } catch (e: Exception) {
            onError()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = PlayerState.PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = PlayerState.PAUSED
    }

    override fun releasePlayer() {
        mediaPlayer.release()
        playerState = PlayerState.DEFAULT
    }

    override fun seekTo(msec: Int) {
        mediaPlayer.seekTo(msec)
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun getPlayerState(): PlayerState {
        return playerState
    }
}
