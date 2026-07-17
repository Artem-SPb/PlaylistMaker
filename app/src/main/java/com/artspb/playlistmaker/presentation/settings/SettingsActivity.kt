package com.artspb.playlistmaker.presentation.settings

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.artspb.playlistmaker.Creator
import com.artspb.playlistmaker.R
import com.artspb.playlistmaker.domain.models.ThemeSettings
import com.artspb.playlistmaker.domain.settings.SettingsInteractor
import com.artspb.playlistmaker.domain.sharing.SharingInteractor
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {

    // Получаем интеракторы из Creator (Clean Architecture)
    private val settingsInteractor: SettingsInteractor = Creator.provideSettingsInteractor()
    private val sharingInteractor: SharingInteractor = Creator.provideSharingInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        themeSwitcher.isChecked = settingsInteractor.getThemeSettings().darkTheme

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            settingsInteractor.updateThemeSetting(ThemeSettings(checked))
        }

        val shareButton = findViewById<MaterialTextView>(R.id.btnShare)
        val supportButton = findViewById<MaterialTextView>(R.id.btnSupport)
        val agreementButton = findViewById<MaterialTextView>(R.id.btnAgreement)

        shareButton.setOnClickListener {
            sharingInteractor.shareApp()
        }

        supportButton.setOnClickListener {
            sharingInteractor.openSupport()
        }

        agreementButton.setOnClickListener {
            sharingInteractor.openTerms()
        }
    }
}
