package com.artspb.playlistmaker.search.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.artspb.playlistmaker.search.domain.models.Track

/**
 * Адаптер для отображения списка треков, истории прослушиваний и треков внутри плейлиста (слой Presentation).
 *
 * Почему я сделал именно так:
 * Переиспользую `TrackViewHolder` и вынес опциональный `longClickListener` со значением по умолчанию `null`
 * прямо в основной конструктор, чтобы на экране плейлиста обрабатывать долгое нажатие без лишних конструкторов.
 */
class TrackAdapter(
    private val clickListener: (Track) -> Unit,
    private val longClickListener: ((Track) -> Boolean)? = null
) : RecyclerView.Adapter<TrackViewHolder>() {

    var tracks: MutableList<Track> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun getItemCount(): Int = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            clickListener.invoke(track)
        }
        holder.itemView.setOnLongClickListener {
            longClickListener?.invoke(track) ?: false
        }
    }
}
