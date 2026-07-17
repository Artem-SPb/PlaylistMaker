package com.artspb.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import com.artspb.playlistmaker.data.history.SearchHistoryRepositoryImpl
import com.artspb.playlistmaker.data.network.RetrofitNetworkClient
import com.artspb.playlistmaker.data.player.AndroidAudioPlayerImpl
import com.artspb.playlistmaker.data.search.TracksRepositoryImpl
import com.artspb.playlistmaker.data.settings.SettingsRepositoryImpl
import com.artspb.playlistmaker.data.sharing.ExternalNavigatorImpl
import com.artspb.playlistmaker.domain.history.SearchHistoryInteractor
import com.artspb.playlistmaker.domain.history.SearchHistoryInteractorImpl
import com.artspb.playlistmaker.domain.history.SearchHistoryRepository
import com.artspb.playlistmaker.domain.player.AudioPlayerInteractor
import com.artspb.playlistmaker.domain.player.AudioPlayerInteractorImpl
import com.artspb.playlistmaker.domain.search.TracksInteractor
import com.artspb.playlistmaker.domain.search.TracksInteractorImpl
import com.artspb.playlistmaker.domain.search.TracksRepository
import com.artspb.playlistmaker.domain.settings.SettingsInteractor
import com.artspb.playlistmaker.domain.settings.SettingsInteractorImpl
import com.artspb.playlistmaker.domain.settings.SettingsRepository
import com.artspb.playlistmaker.domain.sharing.ExternalNavigator
import com.artspb.playlistmaker.domain.sharing.SharingInteractor
import com.artspb.playlistmaker.domain.sharing.SharingInteractorImpl
import com.google.gson.Gson

/**
 * Локатор зависимостей (Creator), который я создал по рекомендации ревьюера и учебному примеру.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Реализую здесь паттерн Service Locator (как предтечу полноценного Dependency Injection), чтобы соблюсти принцип DIP.
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
