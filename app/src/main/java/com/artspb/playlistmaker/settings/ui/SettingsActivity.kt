package com.artspb.playlistmaker.settings.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.artspb.playlistmaker.App
import com.artspb.playlistmaker.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel by viewModel<SettingsViewModel>()

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

        viewModel.themeSettingsState.observe(this) { themeSettings ->
            themeSwitcher.isChecked = themeSettings.darkTheme
            // Здесь я применяю смену темы для всего приложения в реальном времени, 
            // так как стартовая инициализация темы происходит в App.kt
            (applicationContext as App).switchTheme(themeSettings.darkTheme)
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.updateThemeSetting(checked)
        }

        val shareButton = findViewById<MaterialTextView>(R.id.btnShare)
        val supportButton = findViewById<MaterialTextView>(R.id.btnSupport)
        val agreementButton = findViewById<MaterialTextView>(R.id.btnAgreement)

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
