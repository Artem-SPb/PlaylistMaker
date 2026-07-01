package com.artspb.playlistmaker

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class MediaActivity : AppCompatActivity() {

    // Кэшируем форматтер, чтобы не создавать объект каждый раз.
    // Это хорошая практика для оптимизации памяти при работе с UI.
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    // Переменные для работы с MediaPlayer и обновления таймера (Спринт 14)
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var playButton: ImageButton
    private lateinit var playbackTimeTextView: TextView
    private var url: String? = null
    // Переменные для компенсации сброса таймстемпа в NuPlayer на эмуляторе при стриминге
    private var lastCurrentPosition = 0
    private var positionOffset = 0

    // Runnable для регулярного обновления таймера в формате mm:ss
    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                val currentPosition = mediaPlayer.currentPosition
                // Компенсируем скачок назад, если движок эмулятора (NuPlayer)
                // сбросил метку времени при переключении сетевого буфера
                if (lastCurrentPosition - currentPosition > 1000) {
                    positionOffset += lastCurrentPosition - currentPosition
                }
                lastCurrentPosition = currentPosition

                val displayTime = currentPosition + positionOffset
                playbackTimeTextView.text = dateFormat.format(displayTime)
                handler.postDelayed(this, UPDATE_TIMER_DELAY)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        // 1. Извлекаем данные трека из Intent через интерфейс Parcelable (по рекомендации ревьюера).
        // Начиная с Android 13 (API 33), старый метод getParcelableExtra<T>(String) стал deprecated.
        // Для соблюдения Best Practices и обеспечения обратной совместимости добавляем проверку версии SDK.
        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Track>(EXTRA_TRACK)
        }

        // Безопасный выход, если данные не пришли (защита от крашей)
        if (track == null) {
            finish()
            return
        }

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

        // 6. Инициализация UI и логики аудиоплеера (Спринт 14)
        playButton = findViewById(R.id.playButton)
        playbackTimeTextView = findViewById(R.id.playbackTimeTextView)
        url = track.previewUrl

        preparePlayer()

        playButton.setOnClickListener {
            playbackControl()
        }
    }

    /**
     * Подготовка MediaPlayer к воспроизведению
     */
    private fun preparePlayer() {
        val previewUrl = url
        if (previewUrl.isNullOrEmpty()) {
            playButton.isEnabled = false
            return
        }
        try {
            mediaPlayer.setDataSource(previewUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playButton.isEnabled = true
                playerState = STATE_PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                playerState = STATE_PREPARED
                playButton.setImageResource(R.drawable.ic_play_circle)
                playButton.contentDescription = getString(R.string.player_play_button_description)
                handler.removeCallbacks(updateTimerRunnable)
                lastCurrentPosition = 0
                positionOffset = 0
                playbackTimeTextView.text = getString(R.string.player_default_time)
                mediaPlayer.seekTo(0)
            }
        } catch (e: Exception) {
            playButton.isEnabled = false
        }
    }

    /**
     * Управление воспроизведением по клику на кнопку Play/Pause
     */
    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startPlayer() {
        if (playerState == STATE_PREPARED) {
            lastCurrentPosition = 0
            positionOffset = 0
        }
        mediaPlayer.start()
        playerState = STATE_PLAYING
        playButton.setImageResource(R.drawable.ic_pause_circle)
        playButton.contentDescription = getString(R.string.player_pause_button_description)
        handler.postDelayed(updateTimerRunnable, UPDATE_TIMER_DELAY)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        playButton.setImageResource(R.drawable.ic_play_circle)
        playButton.contentDescription = getString(R.string.player_play_button_description)
        handler.removeCallbacks(updateTimerRunnable)
    }

    override fun onPause() {
        super.onPause()
        // При переводе приложения в фоновый режим приостанавливаем воспроизведение
        if (playerState == STATE_PLAYING) {
            pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Обязательно освобождаем ресурсы MediaPlayer и удаляем коллбеки при закрытии экрана
        handler.removeCallbacks(updateTimerRunnable)
        mediaPlayer.release()
    }

    companion object {
        // Константа ключа интента для передачи данных трека
        const val EXTRA_TRACK = "extra_track"
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val UPDATE_TIMER_DELAY = 300L
    }
}
