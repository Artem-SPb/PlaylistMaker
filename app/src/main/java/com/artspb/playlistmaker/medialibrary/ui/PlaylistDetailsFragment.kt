package com.artspb.playlistmaker.medialibrary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.artspb.playlistmaker.R
import com.artspb.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.artspb.playlistmaker.medialibrary.domain.models.Playlist
import com.artspb.playlistmaker.search.domain.models.Track
import com.artspb.playlistmaker.search.ui.TrackAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Мой фрагмент экрана «Плейлист» для просмотра информации о плейлисте, списка его треков и управления им (слой UI).
 *
 * Почему я сделал именно так:
 * 1. Использую два `BottomSheetBehavior`: первый (`tracksBottomSheet`, `hideable = false`) отображает список треков,
 *    второй (`menuBottomSheet`, `hideable = true`) — всплывающее меню с действиями по нажатию на три точки.
 * 2. Для общей продолжительности складываю `trackTimeMillis` всех треков и форматирую через `SimpleDateFormat("mm", Locale.getDefault())`,
 *    как указано в подсказках задания.
 */
class PlaylistDetailsFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: PlaylistDetailsFragmentArgs by navArgs()

    private val viewModel by viewModel<PlaylistDetailsViewModel> {
        parametersOf(args.playlistId)
    }

    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val trackDurationFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val totalMinutesFormat by lazy { SimpleDateFormat("mm", Locale.getDefault()) }

    private var isClickAllowed = true

    private val trackAdapter by lazy {
        TrackAdapter(
            clickListener = { track -> onTrackClicked(track) },
            longClickListener = { track ->
                showDeleteTrackDialog(track)
                true
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTracksBottomSheet()
        setupMenuBottomSheet()

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.shareButton.setOnClickListener {
            handleSharePlaylist()
        }

        binding.menuButton.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            if (playlist != null) {
                renderPlaylistInfo(playlist)
                updateStats()
            }
        }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            renderTracks(tracks)
            updateStats()
        }

        viewModel.playlistDeleted.observe(viewLifecycleOwner) { deleted ->
            if (deleted == true) {
                findNavController().navigateUp()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isClickAllowed = true
    }

    private fun setupTracksBottomSheet() {
        binding.tracksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.tracksRecyclerView.adapter = trackAdapter

        tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet)

        binding.root.post {
            val b = _binding ?: return@post
            val bottomOfButtons = b.shareButton.bottom
            val totalHeight = b.root.height
            val marginPx = (24 * resources.displayMetrics.density).toInt()
            val dynamicPeekHeight = (totalHeight - bottomOfButtons - marginPx).coerceAtLeast(
                (180 * resources.displayMetrics.density).toInt()
            )
            tracksBottomSheetBehavior.peekHeight = dynamicPeekHeight
        }
    }

    private fun setupMenuBottomSheet() {
        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                _binding?.overlay?.isVisible = newState != BottomSheetBehavior.STATE_HIDDEN
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                _binding?.overlay?.alpha = ((slideOffset + 1f) / 2f).coerceIn(0f, 1f)
            }
        })

        binding.overlay.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.menuShareTextView.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            handleSharePlaylist()
        }

        binding.menuEditTextView.setOnClickListener {
            val currentPlaylist = viewModel.playlist.value ?: return@setOnClickListener
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val action = PlaylistDetailsFragmentDirections
                .actionPlaylistDetailsFragmentToEditPlaylistFragment(currentPlaylist)
            findNavController().navigate(action)
        }

        binding.menuDeleteTextView.setOnClickListener {
            val currentPlaylist = viewModel.playlist.value ?: return@setOnClickListener
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDeletePlaylistDialog(currentPlaylist)
        }
    }

    private fun renderPlaylistInfo(playlist: Playlist) {
        binding.playlistNameTextView.text = playlist.name

        if (playlist.description.isNotBlank()) {
            binding.playlistDescriptionTextView.text = playlist.description
            binding.playlistDescriptionTextView.isVisible = true
        } else {
            binding.playlistDescriptionTextView.isVisible = false
        }

        val imageFile = if (!playlist.coverImagePath.isNullOrEmpty()) {
            File(playlist.coverImagePath)
        } else {
            null
        }

        if (imageFile != null && imageFile.exists()) {
            binding.playlistCoverImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(this)
                .load(imageFile)
                .placeholder(R.drawable.ic_placeholder_large)
                .centerCrop()
                .into(binding.playlistCoverImageView)
        } else {
            binding.playlistCoverImageView.scaleType = ImageView.ScaleType.FIT_CENTER
            Glide.with(this).clear(binding.playlistCoverImageView)
            binding.playlistCoverImageView.setImageResource(R.drawable.ic_placeholder_large)
        }

        // Обновляем карточку плейлиста в шапке меню Bottom Sheet
        binding.menuPlaylistHeader.playlistNameTextView.text = playlist.name
        binding.menuPlaylistHeader.playlistTracksCountTextView.text = resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.tracksCount,
            playlist.tracksCount
        )
        val miniCornerRadius = resources.getDimensionPixelSize(R.dimen.track_cover_corner_radius)
        Glide.with(this)
            .load(imageFile)
            .placeholder(R.drawable.ic_placeholder)
            .transform(CenterCrop(), RoundedCorners(miniCornerRadius))
            .into(binding.menuPlaylistHeader.playlistCoverImageView)
    }

    private fun renderTracks(tracks: List<Track>) {
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()

        val isEmpty = tracks.isEmpty()
        binding.emptyTracksPlaceholder.isVisible = isEmpty
        binding.tracksRecyclerView.isVisible = !isEmpty
    }

    private fun updateStats() {
        val tracks = viewModel.tracks.value.orEmpty()
        val durationSum = tracks.sumOf { it.trackTimeMillis }
        val minutes = totalMinutesFormat.format(durationSum).toIntOrNull() ?: 0

        val minutesString = resources.getQuantityString(R.plurals.minutes_count, minutes, minutes)
        val tracksCount = tracks.size
        val tracksString = resources.getQuantityString(R.plurals.tracks_count, tracksCount, tracksCount)

        binding.playlistStatsTextView.text = "$minutesString • $tracksString"
    }

    private fun handleSharePlaylist() {
        val currentPlaylist = viewModel.playlist.value ?: return
        val tracks = viewModel.tracks.value.orEmpty()

        if (tracks.isEmpty()) {
            showPlaylistSnackbar(
                binding.root,
                getString(R.string.empty_playlist_share_message)
            )
            return
        }

        val shareMessage = buildString {
            appendLine(currentPlaylist.name)
            if (currentPlaylist.description.isNotBlank()) {
                appendLine(currentPlaylist.description)
            }
            val tracksCountText = resources.getQuantityString(
                R.plurals.tracks_count,
                tracks.size,
                tracks.size
            )
            appendLine(tracksCountText)
            tracks.forEachIndexed { index, track ->
                val formattedDuration = trackDurationFormat.format(track.trackTimeMillis)
                append("${index + 1}. ${track.artistName} - ${track.trackName} ($formattedDuration)")
                if (index < tracks.lastIndex) {
                    appendLine()
                }
            }
        }

        viewModel.shareText(shareMessage)
    }

    private fun onTrackClicked(track: Track) {
        if (!clickDebounce()) return
        val action = PlaylistDetailsFragmentDirections
            .actionPlaylistDetailsFragmentToAudioPlayerFragment(track)
        findNavController().navigate(action)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.delete_track_dialog_message)
            .setNegativeButton(R.string.dialog_no) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.dialog_yes) { _, _ ->
                viewModel.deleteTrack(track)
            }
            .show()
    }

    private fun showDeletePlaylistDialog(playlist: Playlist) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_playlist_dialog_title)
            .setMessage(getString(R.string.delete_playlist_dialog_message, playlist.name))
            .setNegativeButton(R.string.dialog_no) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.dialog_yes) { _, _ ->
                viewModel.deletePlaylist()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
