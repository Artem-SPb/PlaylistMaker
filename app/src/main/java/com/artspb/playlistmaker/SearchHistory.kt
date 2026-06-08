package com.artspb.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) {
    // Читаем историю из памяти. Если там пусто, возвращаю пустой ArrayList
    fun getHistory(): ArrayList<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return ArrayList()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    // Логика добавления трека в историю
    fun addTrack(track: Track) {
        val history = getHistory()

        // Удаляю трек из списка, если он уже там был (чтобы не было дубликатов)
        history.removeAll { it.trackId == track.trackId }

        // Добавляю трек в самое начало (индекс 0)
        history.add(0, track)

        // Если история стала больше 10 элементов, удаляю самый старый (последний)
        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.size - 1)
        }

        saveHistory(history)
    }

    // Полная очистка истории
    fun clearHistory() {
        sharedPreferences.edit().remove(HISTORY_KEY).apply()
    }

    // Приватный метод для сохранения обновленного списка обратно в SharedPreferences
    private fun saveHistory(history: ArrayList<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit().putString(HISTORY_KEY, json).apply()
    }

    companion object {
        const val HISTORY_KEY = "search_history_key"
        const val MAX_HISTORY_SIZE = 10
    }
}