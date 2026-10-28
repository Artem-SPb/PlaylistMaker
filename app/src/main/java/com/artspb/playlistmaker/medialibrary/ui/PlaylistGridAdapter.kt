package com.artspb.playlistmaker.medialibrary.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.artspb.playlistmaker.medialibrary.domain.models.Playlist

/**
 * Адаптер для отображения сетки плейлистов в 2 колонки на экране Медиатеки (слой Presentation).
 */
class PlaylistGridAdapter(
    private val clickListener: (Playlist) -> Unit = {}
) : RecyclerView.Adapter<PlaylistGridViewHolder>() {

    val playlists: MutableList<Playlist> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistGridViewHolder {
        return PlaylistGridViewHolder(parent)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistGridViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            clickListener(playlist)
        }
    }
}
