package com.artspb.playlistmaker.data.player

import android.media.MediaPlayer
import com.artspb.playlistmaker.domain.player.AudioPlayerControl
import com.artspb.playlistmaker.domain.player.PlayerState

/**
 * Реализация контроллера `AudioPlayerControl` на базе стандартного Android `MediaPlayer` в слое Data.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. `MediaPlayer` является частью Android SDK (инфраструктурный слой), поэтому его реализацию я перенес сюда, в `data.player`.
 * 2. Вся низкоуровневая специфика работы с аудиопотоком надежно скрыта за абстракцией слоя Domain.
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
