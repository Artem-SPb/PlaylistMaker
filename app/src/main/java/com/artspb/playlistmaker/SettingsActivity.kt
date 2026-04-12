package com.artspb.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // --- 1. Находим кнопку "Назад" ---
        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish() // Закрываем этот экран и возвращаемся на предыдущий
        }

        // --- 2. Кнопка "Поделиться приложением" ---
        val shareButton = findViewById<LinearLayout>(R.id.share_button)
        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.course_link))
            startActivity(Intent.createChooser(shareIntent, "Поделиться приложением"))
        }

        // --- 3. Кнопка "Написать в поддержку" ---
        val supportButton = findViewById<LinearLayout>(R.id.support_button)
        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))
            startActivity(supportIntent)
        }

        // --- 4. Кнопка "Пользовательское соглашение" ---
        val termsButton = findViewById<LinearLayout>(R.id.terms_button)
        termsButton.setOnClickListener {
            val termsIntent = Intent(Intent.ACTION_VIEW)
            termsIntent.data = Uri.parse(getString(R.string.practicum_offer_link))
            startActivity(termsIntent)
        }
    }
}
