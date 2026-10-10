package com.artspb.playlistmaker.search.domain

import com.artspb.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс интерактора поиска треков (контракт бизнес-логики для слоя Presentation).
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Мой UI-слой общается с бизнес-логикой исключительно через этот контракт,
 *    что обеспечивает слабую связность и соблюдение принципа инверсии зависимостей (DIP).
 * 2. Вместо устаревших коллбеков (`TracksConsumer`) я теперь возвращаю `Flow`.
 */
interface TracksInteractor {
    fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>>
}
