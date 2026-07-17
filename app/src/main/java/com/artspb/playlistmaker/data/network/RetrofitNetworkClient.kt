package com.artspb.playlistmaker.data.network

import com.artspb.playlistmaker.data.dto.Response
import com.artspb.playlistmaker.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Моя реализация сетевого клиента на базе Retrofit в слое Data.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Выполняю синхронный запрос `execute()`, так как вызов метода `doRequest` уже происходит
 *    в фоновом потоке из интерактора (через `ExecutorService`).
 * 2. Все исключения сети (например, отсутствие интернета) я отлавливаю внутри и возвращаю `resultCode = 500`.
 *    Это гарантирует, что приложение не упадет, а UI сможет корректно показать плейсхолдер ошибки.
 */
class RetrofitNetworkClient : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApi::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto !is TracksSearchRequest) {
            return Response().apply { resultCode = 400 }
        }

        return try {
            val response = itunesService.search(dto.expression).execute()
            val body = response.body()
            if (body != null && response.isSuccessful) {
                body.apply { resultCode = response.code() }
            } else {
                Response().apply { resultCode = response.code() }
            }
        } catch (e: Exception) {
            Response().apply { resultCode = 500 }
        }
    }

    private companion object {
        const val ITUNES_BASE_URL = "https://itunes.apple.com"
    }
}
