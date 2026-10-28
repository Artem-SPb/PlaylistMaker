package com.artspb.playlistmaker.di

import com.artspb.playlistmaker.medialibrary.ui.CreatePlaylistViewModel
import com.artspb.playlistmaker.medialibrary.ui.FavoritesViewModel
import com.artspb.playlistmaker.medialibrary.ui.PlaylistsViewModel
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

    viewModel {
        FavoritesViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        CreatePlaylistViewModel(get())
    }

    viewModel { (track: Track) ->
        MediaViewModel(track, get(), get(), get())
    }
}
