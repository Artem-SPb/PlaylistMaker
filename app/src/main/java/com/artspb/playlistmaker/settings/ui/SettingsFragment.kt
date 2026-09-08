package com.artspb.playlistmaker.settings.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.artspb.playlistmaker.App
import com.artspb.playlistmaker.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.updatePadding(top = statusBar.top)
            insets
        }

        val themeSwitcher = view.findViewById<SwitchMaterial>(R.id.themeSwitcher)

        viewModel.themeSettingsState.observe(viewLifecycleOwner) { themeSettings ->
            themeSwitcher.isChecked = themeSettings.darkTheme
            (requireContext().applicationContext as App).switchTheme(themeSettings.darkTheme)
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.updateThemeSetting(checked)
        }

        val shareButton = view.findViewById<MaterialTextView>(R.id.btnShare)
        val supportButton = view.findViewById<MaterialTextView>(R.id.btnSupport)
        val agreementButton = view.findViewById<MaterialTextView>(R.id.btnAgreement)

        shareButton.setOnClickListener {
            viewModel.shareApp()
        }

        supportButton.setOnClickListener {
            viewModel.openSupport()
        }

        agreementButton.setOnClickListener {
            viewModel.openTerms()
        }
    }
}
