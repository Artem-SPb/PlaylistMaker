package com.artspb.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

// Добавили в конструктор clickListener - лямбду, которая принимает Track и ничего не возвращает (Unit)
class TrackAdapter(
    private val clickListener: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun getItemCount(): Int = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        // Отрисовываем трек
        holder.bind(tracks[position])

        // Вешаем слушатель клика на весь элемент списка (itemView)
        holder.itemView.setOnClickListener {
            // При клике вызываем лямбду и передаем в нее трек, по которому кликнули
            clickListener.invoke(tracks[position])
        }
    }
}
