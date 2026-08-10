package com.artspb.playlistmaker.di

import android.content.Context
import com.artspb.playlistmaker.PLAYLIST_MAKER_PREFERENCES
import com.artspb.playlistmaker.player.data.AndroidAudioPlayerImpl
import com.artspb.playlistmaker.player.domain.AudioPlayerControl

import com.artspb.playlistmaker.search.data.network.ItunesApi
import com.artspb.playlistmaker.search.data.network.NetworkClient
import com.artspb.playlistmaker.search.data.network.RetrofitNetworkClient
import com.artspb.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.artspb.playlistmaker.sharing.domain.ExternalNavigator
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

    factory<AudioPlayerControl> {
        AndroidAudioPlayerImpl()
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }
}
