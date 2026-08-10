package com.artspb.playlistmaker.di

import com.artspb.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.artspb.playlistmaker.search.data.TracksRepositoryImpl
import com.artspb.playlistmaker.search.domain.SearchHistoryRepository
import com.artspb.playlistmaker.search.domain.TracksRepository
import com.artspb.playlistmaker.settings.data.SettingsRepositoryImpl
import com.artspb.playlistmaker.settings.domain.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get(), androidContext() as com.artspb.playlistmaker.App)
    }
}
