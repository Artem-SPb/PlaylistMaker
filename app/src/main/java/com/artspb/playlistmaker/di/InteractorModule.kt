package com.artspb.playlistmaker.di

import com.artspb.playlistmaker.player.domain.AudioPlayerInteractor
import com.artspb.playlistmaker.player.domain.AudioPlayerInteractorImpl
import com.artspb.playlistmaker.search.domain.SearchHistoryInteractor
import com.artspb.playlistmaker.search.domain.SearchHistoryInteractorImpl
import com.artspb.playlistmaker.search.domain.TracksInteractor
import com.artspb.playlistmaker.search.domain.TracksInteractorImpl
import com.artspb.playlistmaker.settings.domain.SettingsInteractor
import com.artspb.playlistmaker.settings.domain.SettingsInteractorImpl
import com.artspb.playlistmaker.sharing.domain.SharingInteractor
import com.artspb.playlistmaker.sharing.domain.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    factory<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    factory<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    factory<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    factory<AudioPlayerInteractor> {
        AudioPlayerInteractorImpl(get())
    }
}
