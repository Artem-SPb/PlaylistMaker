package com.artspb.playlistmaker

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

    // Контроллер аудиоплеера, вынесенный за абстракцию (интерфейс) по рекомендации ревьюера (Спринт 14).
    // Позволяет легко подменить реализацию (например, на ExoPlayer) и упрощает тестирование.
    private val audioPlayer: AudioPlayerControl = AndroidAudioPlayerImpl()

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
            if (audioPlayer.getPlayerState() == PlayerState.PLAYING) {
                val currentPosition = audioPlayer.getCurrentPosition()
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
        val albumGroup = findViewById<Group>(R.id.albumGroup)
        val albumValue = findViewById<TextView>(R.id.albumValue)
        val yearValue = findViewById<TextView>(R.id.yearValue)
        val genreValue = findViewById<TextView>(R.id.genreValue)
        val countryValue = findViewById<TextView>(R.id.countryValue)

        // 3. Настройка кнопки Назад
        backButton.setOnClickListener {
            finish()
        }

        // 4. Заполнение обязательных текстовых полей
        trackName.text = track.trackName
        artistName.text = track.artistName
        durationValue.text = dateFormat.format(track.trackTimeMillis)
        genreValue.text = track.primaryGenreName
        countryValue.text = track.country

        // 5. Логика отображения опциональных полей (Альбом и Год)
        if (!track.collectionName.isNullOrEmpty()) {
            albumValue.text = track.collectionName
            albumGroup.visibility = View.VISIBLE
        } else {
            albumGroup.visibility = View.GONE
        }

        if (!track.releaseDate.isNullOrEmpty() && track.releaseDate.length >= 4) {
            yearValue.text = track.releaseDate.substring(0, 4)
        } else {
            yearValue.text = ""
        }

        // Загрузка обложки в разрешении 512x512 через Glide
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
     * Подготовка плеера к воспроизведению через абстрактный интерфейс AudioPlayerControl
     */
    private fun preparePlayer() {
        val previewUrl = url
        if (previewUrl.isNullOrEmpty()) {
            playButton.isEnabled = false
            return
        }
        audioPlayer.preparePlayer(
            url = previewUrl,
            onPrepared = {
                playButton.isEnabled = true
            },
            onCompletion = {
                playButton.setImageResource(R.drawable.ic_play_circle)
                playButton.contentDescription = getString(R.string.player_play_button_description)
                handler.removeCallbacks(updateTimerRunnable)
                lastCurrentPosition = 0
                positionOffset = 0
                playbackTimeTextView.text = getString(R.string.player_default_time)
                audioPlayer.seekTo(0)
            },
            onError = {
                playButton.isEnabled = false
            }
        )
    }

    /**
     * Управление воспроизведением по клику на кнопку Play/Pause с использованием enum PlayerState
     */
    private fun playbackControl() {
        when (audioPlayer.getPlayerState()) {
            PlayerState.PLAYING -> {
                pausePlayer()
            }
            PlayerState.PREPARED, PlayerState.PAUSED -> {
                startPlayer()
            }
            PlayerState.DEFAULT -> {
                // Плеер еще не подготовлен
            }
        }
    }

    private fun startPlayer() {
        if (audioPlayer.getPlayerState() == PlayerState.PREPARED) {
            lastCurrentPosition = 0
            positionOffset = 0
        }
        audioPlayer.startPlayer()
        playButton.setImageResource(R.drawable.ic_pause_circle)
        playButton.contentDescription = getString(R.string.player_pause_button_description)
        handler.postDelayed(updateTimerRunnable, UPDATE_TIMER_DELAY)
    }

    private fun pausePlayer() {
        audioPlayer.pausePlayer()
        playButton.setImageResource(R.drawable.ic_play_circle)
        playButton.contentDescription = getString(R.string.player_play_button_description)
        handler.removeCallbacks(updateTimerRunnable)
    }

    override fun onPause() {
        super.onPause()
        // При переводе приложения в фоновый режим приостанавливаем воспроизведение
        if (audioPlayer.getPlayerState() == PlayerState.PLAYING) {
            pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Обязательно освобождаем ресурсы плеера и удаляем коллбеки при закрытии экрана
        handler.removeCallbacks(updateTimerRunnable)
        audioPlayer.releasePlayer()
    }

    companion object {
        // Константа ключа интента для передачи данных трека
        const val EXTRA_TRACK = "extra_track"
        private const val UPDATE_TIMER_DELAY = 300L
    }
}
