package com.artspb.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Включаю EdgeToEdge, как и на экране поиска (требование для всего приложения)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        // Делаю отступ от статус-бара
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        // Нахожу тулбар и вешаю кнопку "Назад"
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val shareButton = findViewById<MaterialTextView>(R.id.btnShare)
        val supportButton = findViewById<MaterialTextView>(R.id.btnSupport)
        val agreementButton = findViewById<MaterialTextView>(R.id.btnAgreement)

        // Кейс 1: Поделиться приложением
        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.course_link))

            // Ревьюер указал, что заголовок диалога ("Поделиться приложением")
            // нужно брать из ресурсов. Использую getString(R.string.share_app).
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app)))
        }

        // Кейс 2: Написать в поддержку (код оставляешь свой, который был написан ранее, главное не хардкодь строки!)
        supportButton.setOnClickListener {
            // ... твой код интента ACTION_SENDTO с mailto:
        }

        // Кейс 3: Пользовательское соглашение
        agreementButton.setOnClickListener {
            // ... твой код интента ACTION_VIEW
        }
    }
}
