package com.artspb.playlistmaker.data.network

import com.artspb.playlistmaker.data.dto.Response

/**
 * Интерфейс сетевого клиента, который я выделил в слое Data по принципам Clean Architecture.
 *
 * Почему я сделал именно так:
 * 1. Мой репозиторий зависит от этой абстракции, а не от конкретного Retrofit или OkHttp.
 * 2. Благодаря этому я могу в любой момент заменить библиотеку для работы с сетью или подсунуть `MockNetworkClient`
 *    в тестах, вообще не трогая код репозиториев и интеракторов.
 */
interface NetworkClient {
    fun doRequest(dto: Any): Response
}
