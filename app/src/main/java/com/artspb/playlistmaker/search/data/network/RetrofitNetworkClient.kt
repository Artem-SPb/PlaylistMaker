package com.artspb.playlistmaker.search.data.network

import com.artspb.playlistmaker.search.data.dto.Response
import com.artspb.playlistmaker.search.data.dto.TracksSearchRequest


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Моя реализация сетевого клиента на базе Retrofit в слое Data.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Выполняю синхронный запрос `execute()`, так как вызов метода `doRequest` уже происходит
 *    в фоновом потоке из интерактора (через `ExecutorService`).
 * 2. Добавил проверку интернет-соединения через ConnectivityManager, как рекомендовали в теории 17 спринта.
 */
class RetrofitNetworkClient(
    private val itunesService: ItunesApi,
    private val context: Context
) : NetworkClient {

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }

    override fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = -1 }
        }

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
