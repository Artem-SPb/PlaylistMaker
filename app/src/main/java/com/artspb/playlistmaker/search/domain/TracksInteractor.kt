package com.artspb.playlistmaker.search.domain

import com.artspb.playlistmaker.search.domain.models.Track

/**
 * Интерфейс интерактора поиска треков (контракт бизнес-логики для слоя Presentation).
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Мой UI-слой (`SearchActivity`) общается с бизнес-логикой исключительно через этот контракт,
 *    что обеспечивает слабую связность и соблюдение принципа инверсии зависимостей (DIP).
 * 2. Асинхронный результат возвращается через коллбек `TracksConsumer` точно так же,
 *    как в учебном примере `MovieSearchApp`.
 */
interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
}
