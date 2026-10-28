package com.artspb.playlistmaker.medialibrary.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.artspb.playlistmaker.R
import com.artspb.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Мой фрагмент для экрана создания нового плейлиста (слой UI).
 *
 * Почему я сделал именно так:
 * 1. Использую стандартный системный Photo Picker (`PickVisualMedia`) для выбора обложки из галереи без лишних разрешений.
 * 2. Кнопка «Создать» активируется только тогда, когда поле «Название» не пустое.
 * 3. Перехватываю нажатие кнопки «Назад» (как тулбара, так и системной через `OnBackPressedCallback`):
 *    если пользователь уже выбрал обложку или ввёл текст, показываю диалог подтверждения выхода.
 */
class CreatePlaylistFragment : Fragment() {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<CreatePlaylistViewModel>()

    private var selectedImageUri: Uri? = null
    private var previousSoftInputMode: Int? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            showSelectedCover(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        previousSoftInputMode = requireActivity().window.attributes.softInputMode
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        savedInstanceState?.getString(KEY_SELECTED_URI)?.let { uriString ->
            val uri = Uri.parse(uriString)
            selectedImageUri = uri
            showSelectedCover(uri)
        }

        binding.coverImageView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.nameEditText.doOnTextChanged { text, _, _, _ ->
            val isNotEmpty = !text.isNullOrBlank()
            binding.createButton.isEnabled = isNotEmpty
            binding.nameInputLayout.isActivated = isNotEmpty
        }

        binding.descriptionEditText.doOnTextChanged { text, _, _, _ ->
            binding.descriptionInputLayout.isActivated = !text.isNullOrBlank()
        }

        binding.createButton.setOnClickListener {
            val name = binding.nameEditText.text?.toString().orEmpty()
            val description = binding.descriptionEditText.text?.toString().orEmpty()
            if (name.isNotBlank()) {
                binding.createButton.isEnabled = false
                viewModel.createPlaylist(name, description, selectedImageUri?.toString())
            }
        }

        binding.backButton.setOnClickListener {
            handleBackNavigation()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackNavigation()
                }
            }
        )

        viewModel.playlistCreated.observe(viewLifecycleOwner) { createdPlaylistName ->
            if (createdPlaylistName != null) {
                val rootView = requireActivity().findViewById<View>(R.id.main)
                showPlaylistSnackbar(
                    rootView,
                    getString(R.string.playlist_created_toast, createdPlaylistName)
                )
                findNavController().navigateUp()
            }
        }
    }

    private fun showSelectedCover(uri: Uri) {
        val cornerRadius = resources.getDimensionPixelSize(R.dimen.player_cover_corner_radius)
        binding.coverImageView.background = null
        Glide.with(this)
            .load(uri)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .into(binding.coverImageView)
    }

    private fun hasUnsavedChanges(): Boolean {
        val hasImage = selectedImageUri != null
        val hasName = !binding.nameEditText.text.isNullOrEmpty()
        val hasDescription = !binding.descriptionEditText.text.isNullOrEmpty()
        return hasImage || hasName || hasDescription
    }

    private fun handleBackNavigation() {
        if (hasUnsavedChanges()) {
            showExitConfirmationDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.finish_playlist_creation_title)
            .setMessage(R.string.finish_playlist_creation_message)
            .setNegativeButton(R.string.cancel_button) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.finish_button) { _, _ ->
                findNavController().navigateUp()
            }
            .show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        selectedImageUri?.let {
            outState.putString(KEY_SELECTED_URI, it.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        previousSoftInputMode?.let {
            requireActivity().window.setSoftInputMode(it)
        }
        _binding = null
    }

    companion object {
        private const val KEY_SELECTED_URI = "key_selected_uri"
    }
}
