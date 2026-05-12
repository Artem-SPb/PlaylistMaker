package com.artspb.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat // НОВОЕ: Импортируем форматтер
import java.util.Locale // НОВОЕ: Импортируем локаль

// Я передаю ViewGroup в конструктор и сразу делаю inflate разметки track_item,
class TrackViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context).inflate(
        R.layout.track_item, parentView, false
    )
) {
    // Нахожу все элементы верстки нашей карточки трека
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val trackCover: ImageView = itemView.findViewById(R.id.trackCover)

    // Функция для привязки данных из модели Track к View-элементам
    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName

        // НОВОЕ: Форматирую время из миллисекунд в строку формата "mm:ss"
        // Locale.getDefault() нужен для того, чтобы формат корректно работал на разных языках системы
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)

        // Я использую Glide для асинхронной загрузки обложки по URL.
        // Передаю itemView в метод with() по правилам ТЗ.
        Glide.with(itemView)
            .load(model.artworkUrl100)
            // Показываю плейсхолдер, если нет интернета или картинка грузится
            .placeholder(R.drawable.ic_placeholder)
            // Масштабирую картинку, чтобы она заполнила квадрат 45x45 без искажений
            .centerCrop()
            // Закругляю углы (2dp) без хардкода
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.track_cover_corner_radius)))
            .into(trackCover)
    }
}

