package com.artspb.playlistmaker

/**
 * Интерфейс (контракт) для взаимодействия с аудиоплеером.
 * Написан по рекомендации ревьюера для разделения ответственности (SRP) и декуплирования.
 *
 * Почему это Best Practices:
 * 1. Вынесение работы с системным MediaPlayer за абстракцию позволяет в будущем
 *    легко заменить аудио-движок (например, на ExoPlayer / Media3) без изменения кода в UI (Activity).
 * 2. Упрощает написание Unit-тестов для бизнес-логики (можно подсунуть мок).
 * 3. Является важнейшим шагом к переходу на Clean Architecture и MVVM в 15-м спринте.
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
