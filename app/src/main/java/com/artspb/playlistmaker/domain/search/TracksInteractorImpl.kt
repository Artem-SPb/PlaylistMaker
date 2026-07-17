package com.artspb.playlistmaker.domain.search

import java.util.concurrent.Executors

/**
 * Моя реализация бизнес-логики поиска треков в слое Domain.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Управляю асинхронностью: запускаю синхронный метод репозитория в фоновом потоке из пула
 *    (`Executors.newCachedThreadPool()`) и отдаю результат в коллбек `consumer`.
 * 2. Благодаря этому я полностью отвязал UI от сетевой и потоковой реализации.
 */
class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            val (tracks, errorMessage) = repository.searchTracks(expression)
            consumer.consume(tracks, errorMessage)
        }
    }
}
