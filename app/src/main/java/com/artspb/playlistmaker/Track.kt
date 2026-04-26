package com.artspb.playlistmaker

// Я создаю data class Track, который служит моделью данных для элемента списка.
// В будущем сюда можно будет добавить новые поля из API, но пока оставляю только необходимое для UI.
data class Track(
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTime: String, // Продолжительность трека
    val artworkUrl100: String // Ссылка на изображение обложки (100x100 пикселей)
)
