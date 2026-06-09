package com.artspb.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.artspb.playlistmaker.R
import com.artspb.playlistmaker.Track
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale
class MediaActivity : AppCompatActivity() {

    // Кэшируем форматтер, чтобы не создавать объект каждый раз.
    // Это хорошая практика для оптимизации памяти при работе с UI.
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        // 1. Извлекаем данные трека из Intent (ожидаем, что передали JSON-строку через Gson)
        val trackJson = intent.getStringExtra(EXTRA_TRACK)
        if (trackJson.isNullOrEmpty()) {
            finish() // Безопасный выход, если данных нет
            return
        }
        val track = Gson().fromJson(trackJson, Track::class.java)

        // 2. Инициализируем View компоненты экрана плеера
        val backButton = findViewById<ImageButton>(R.id.backButton)
        val coverImageView = findViewById<ImageView>(R.id.coverImageView)
        val trackName = findViewById<TextView>(R.id.trackNameTextView)
        val artistName = findViewById<TextView>(R.id.artistNameTextView)
        val durationValue = findViewById<TextView>(R.id.durationValue)
        val albumValue = findViewById<TextView>(R.id.albumValue)
        val yearValue = findViewById<TextView>(R.id.yearValue)
        val genreValue = findViewById<TextView>(R.id.genreValue)
        val countryValue = findViewById<TextView>(R.id.countryValue)
        val albumGroup = findViewById<Group>(R.id.albumGroup)

        // 3. Обработка кнопки "Назад"
        // Вызываем finish(), чтобы закрыть текущую Activity и вернуться на предыдущий экран поиска
        backButton.setOnClickListener {
            finish()
        }

        // 4. Заполнение UI элементов данными из нашей модели Track
        trackName.text = track.trackName
        artistName.text = track.artistName
        durationValue.text = dateFormat.format(track.trackTimeMillis)
        genreValue.text = track.primaryGenreName
        countryValue.text = track.country

        // Логика отображения альбома: если данных нет, скрываем всю группу (Заголовок + Значение) через Group
        if (track.collectionName.isNullOrEmpty()) {
            albumGroup.visibility = View.GONE
        } else {
            albumGroup.visibility = View.VISIBLE
            albumValue.text = track.collectionName
        }

        // Логика отображения года: берем первые 4 символа из строки формата "1999-10-12T07:00:00Z"
        if (track.releaseDate.isNullOrEmpty()) {
            yearValue.text = ""
        } else {
            yearValue.text = track.releaseDate.substring(0, 4)
        }

        // 5. Загрузка обложки в высоком разрешении (512x512) через Glide
        // Используем встроенную функцию getCoverArtwork() из модели Track для подмены URL
        val cornerRadius = resources.getDimensionPixelSize(R.dimen.player_cover_corner_radius)
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadius))
            .into(coverImageView)
    }

    companion object {
        const val EXTRA_TRACK = "extra_track"
    }
}