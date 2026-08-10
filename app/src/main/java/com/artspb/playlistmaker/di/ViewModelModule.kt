package com.artspb.playlistmaker.di

import com.artspb.playlistmaker.player.ui.MediaViewModel
import com.artspb.playlistmaker.search.domain.models.Track
import com.artspb.playlistmaker.search.ui.SearchViewModel
import com.artspb.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel { (track: Track) ->
        MediaViewModel(track, get())
    }
}
