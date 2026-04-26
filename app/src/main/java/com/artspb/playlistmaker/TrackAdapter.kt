package com.artspb.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

// Я создаю адаптер для RecyclerView. В конструктор передаю ArrayList с нашими Mock-данными.
class TrackAdapter(
    private val tracks: ArrayList<Track>
) : RecyclerView.Adapter<TrackViewHolder>() {

    // Создаю новый ViewHolder. Родительский ViewGroup передается прямо в конструктор TrackViewHolder,
    // где и происходит создание View.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    // Связываю данные конкретного трека с ViewHolder'ом по позиции в списке
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    // Возвращаю общее количество треков в списке
    override fun getItemCount(): Int {
        return tracks.size
    }
}
