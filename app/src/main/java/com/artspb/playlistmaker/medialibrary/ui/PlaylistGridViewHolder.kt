package com.artspb.playlistmaker.medialibrary.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.artspb.playlistmaker.R
import com.artspb.playlistmaker.medialibrary.domain.models.Playlist
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.io.File

/**
 * ViewHolder для элемента сетки плейлистов в Медиатеке (слой Presentation).
 */
class PlaylistGridViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_playlist_grid, parent, false)
) {
    private val coverImageView: ImageView = itemView.findViewById(R.id.playlistCoverImageView)
    private val nameTextView: TextView = itemView.findViewById(R.id.playlistNameTextView)
    private val tracksCountTextView: TextView = itemView.findViewById(R.id.playlistTracksCountTextView)

    fun bind(playlist: Playlist) {
        nameTextView.text = playlist.name
        tracksCountTextView.text = itemView.resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.tracksCount,
            playlist.tracksCount
        )

        val cornerRadius = itemView.resources.getDimensionPixelSize(R.dimen.player_cover_corner_radius)
        val imageSource = if (!playlist.coverImagePath.isNullOrEmpty()) {
            File(playlist.coverImagePath)
        } else {
            null
        }

        Glide.with(itemView)
            .load(imageSource)
            .placeholder(R.drawable.ic_placeholder)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .into(coverImageView)
    }
}
