package com.artspb.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import com.artspb.playlistmaker.App
import com.artspb.playlistmaker.PLAYLIST_MAKER_PREFERENCES
import com.artspb.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.artspb.playlistmaker.search.data.network.RetrofitNetworkClient
import com.artspb.playlistmaker.player.data.AndroidAudioPlayerImpl
import com.artspb.playlistmaker.search.data.TracksRepositoryImpl
import com.artspb.playlistmaker.settings.data.SettingsRepositoryImpl
import com.artspb.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.artspb.playlistmaker.search.domain.SearchHistoryInteractor
import com.artspb.playlistmaker.search.domain.SearchHistoryInteractorImpl
import com.artspb.playlistmaker.search.domain.SearchHistoryRepository
import com.artspb.playlistmaker.player.domain.AudioPlayerInteractor
import com.artspb.playlistmaker.player.domain.AudioPlayerInteractorImpl
import com.artspb.playlistmaker.search.domain.TracksInteractor
import com.artspb.playlistmaker.search.domain.TracksInteractorImpl
import com.artspb.playlistmaker.search.domain.TracksRepository
import com.artspb.playlistmaker.settings.domain.SettingsInteractor
import com.artspb.playlistmaker.settings.domain.SettingsInteractorImpl
import com.artspb.playlistmaker.settings.domain.SettingsRepository
import com.artspb.playlistmaker.sharing.domain.ExternalNavigator
import com.artspb.playlistmaker.sharing.domain.SharingInteractor
import com.artspb.playlistmaker.sharing.domain.SharingInteractorImpl
import com.google.gson.Gson

/**
 * Локатор зависимостей (Creator), который я создал по рекомендации ревьюера и учебному примеру.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Использую здесь паттерн Service Locator (как мы разбирали в теории), чтобы удобно поставлять зависимости и не связывать слои напрямую.
 * 2. Теперь мои Activity в слое Presentation обращаются только к методам `provide...Interactor()`, вообще не зная о том,
 *    какие именно реализации из слоя Data (Retrofit, SharedPreferences или Gson) работают под капотом.
 */
object Creator {

    private lateinit var application: App

    /**
     * Инициализация контекста приложения для доступа к SharedPreferences и системным ресурсам.
     * Вызывается один раз в App.onCreate().
     */
    fun init(app: App) {
        application = app
    }

    private val sharedPreferences: SharedPreferences by lazy {
        application.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    }

    private val gson: Gson by lazy { Gson() }

    private val tracksRepository: TracksRepository by lazy {
        TracksRepositoryImpl(RetrofitNetworkClient())
    }

    private val searchHistoryRepository: SearchHistoryRepository by lazy {
        SearchHistoryRepositoryImpl(sharedPreferences, gson)
    }

    private val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(sharedPreferences, application)
    }

    private val externalNavigator: ExternalNavigator by lazy {
        ExternalNavigatorImpl(application)
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(tracksRepository)
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(searchHistoryRepository)
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(settingsRepository)
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(externalNavigator)
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(AndroidAudioPlayerImpl())
    }
}
