package com.artspb.playlistmaker.player.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.artspb.playlistmaker.medialibrary.domain.models.Playlist

/**
 * Адаптер для отображения списка плейлистов в Bottom Sheet на экране Аудиоплеера (слой Presentation).
 */
class PlaylistBottomSheetAdapter(
    private val clickListener: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistBottomSheetViewHolder>() {

    val playlists: MutableList<Playlist> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistBottomSheetViewHolder {
        return PlaylistBottomSheetViewHolder(parent)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistBottomSheetViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            clickListener(playlist)
        }
    }
}
