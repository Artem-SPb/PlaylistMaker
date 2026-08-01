package com.artspb.playlistmaker.search.data.dto

/**
 * Объект-запрос (DTO) для передачи строки поиска в сетевой клиент слоя Data.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Оборачиваю параметры запроса в отдельный класс, чтобы метод `doRequest` в `NetworkClient` принимал любой DTO (`Any`).
 * 2. Это позволит мне в будущем легко масштабировать сеть и добавлять новые запросы без изменения интерфейса клиента.
 */
data class TracksSearchRequest(
    val expression: String
)
