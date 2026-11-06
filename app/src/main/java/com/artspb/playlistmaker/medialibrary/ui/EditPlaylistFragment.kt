package com.artspb.playlistmaker.medialibrary.ui

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.artspb.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

/**
 * Мой фрагмент-наследник для экрана редактирования информации о плейлисте (слой UI).
 *
 * Почему я сделал именно так:
 * 1. Наследуюсь от `CreatePlaylistFragment`, меняю заголовок на «Редактировать» и текст кнопки на «Сохранить».
 * 2. При первом открытии предзаполняю обложку, название и описание из переданного плейлиста.
 * 3. Переопределяю `handleBackNavigation()`, чтобы выход происходил сразу без диалога подтверждения, игнорируя несохранённые правки.
 */
class EditPlaylistFragment : CreatePlaylistFragment() {

    private val args: EditPlaylistFragmentArgs by navArgs()

    override val viewModel by viewModel<EditPlaylistViewModel> {
        parametersOf(args.playlist)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.titleTextView.setText(R.string.edit_playlist_title)
        binding.createButton.setText(R.string.save_button)

        viewModel.initialPlaylist.observe(viewLifecycleOwner) { playlist ->
            if (savedInstanceState == null) {
                binding.nameEditText.setText(playlist.name)
                binding.descriptionEditText.setText(playlist.description)
                if (!playlist.coverImagePath.isNullOrEmpty()) {
                    val file = File(playlist.coverImagePath)
                    if (file.exists()) {
                        showSelectedCover(file)
                    }
                }
            } else if (selectedImageUri == null && !playlist.coverImagePath.isNullOrEmpty()) {
                val file = File(playlist.coverImagePath)
                if (file.exists()) {
                    showSelectedCover(file)
                }
            }
        }

        viewModel.playlistUpdated.observe(viewLifecycleOwner) { updated ->
            if (updated == true) {
                findNavController().navigateUp()
            }
        }
    }

    override fun onSubmitClicked() {
        val name = binding.nameEditText.text?.toString().orEmpty()
        val description = binding.descriptionEditText.text?.toString().orEmpty()
        if (name.isNotBlank()) {
            binding.createButton.isEnabled = false
            viewModel.savePlaylistChanges(name, description, selectedImageUri?.toString())
        }
    }

    override fun handleBackNavigation() {
        findNavController().navigateUp()
    }
}
