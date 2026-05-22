package com.artspb.playlistmaker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
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

        // --- РЕАЛИЗАЦИЯ ПЕРЕКЛЮЧЕНИЯ ТЕМЫ ---
        // Нахожу элемент свитчера в разметке экрана настроек
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        // Принудительно устанавливаю текущее состояние свитчера (True/False)
        // беру значение напрямую из нашего глобального класса App, который инициализировался при старте
        themeSwitcher.isChecked = (applicationContext as App).darkTheme

        // Вешаю слушатель изменения состояния свитчера
        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            // 1. Вызываю метод динамического переключения темы в нашем Application классе
            (applicationContext as App).switchTheme(checked)

            // 2. Записываю новое состояние темы в SharedPreferences, чтобы при следующем запуске
            // приложение "вспомнило" выбор пользователя
            val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
            sharedPrefs.edit()
                .putBoolean(DARK_THEME_KEY, checked)
                .apply() // Использую apply() для асинхронной и безопасной записи в фоне
        }


        // --- РЕАЛИЗАЦИЯ НЕЯВНЫХ ИНТЕНТОВ ---
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

        // Кейс 2: Написать в поддержку
        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:") // Гарантирует, что интент обработают только почтовые клиенты
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))
            }
            startActivity(supportIntent)
        }

        // Кейс 3: Пользовательское соглашение
        agreementButton.setOnClickListener {
            val agreementIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.practicum_offer_link))
            }
            startActivity(agreementIntent)
        }
    }
}
