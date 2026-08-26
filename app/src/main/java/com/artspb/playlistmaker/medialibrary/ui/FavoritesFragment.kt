package com.artspb.playlistmaker.medialibrary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.artspb.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    // Подключаем ViewModel через Koin (пока она пустая, но готова к работе)
    private val viewModel by viewModel<FavoritesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Надуваем нашу верстку с заглушкой для избранных треков
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    companion object {
        // Фабричный метод для создания фрагмента
        fun newInstance() = FavoritesFragment()
    }
}
