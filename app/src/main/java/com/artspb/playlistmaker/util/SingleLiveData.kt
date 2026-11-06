package com.artspb.playlistmaker.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Моя реализация `SingleLiveData` для одноразовых событий (One-shot events) в слое Presentation.
 *
 * Почему я использую её вместо обычной `MutableLiveData`:
 * При пересоздании экрана или повороте устройства обычная `LiveData` повторно отправляет последнее значение,
 * что может вызвать лишний показ Snackbar или повторный возврат по стеку навигации.
 * `AtomicBoolean` гарантирует, что событие будет обработано ровно один раз.
 */
class SingleLiveData<T> : MutableLiveData<T>() {

    private val pending = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner) { value ->
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(value)
            }
        }
    }

    override fun setValue(t: T?) {
        pending.set(true)
        super.setValue(t)
    }
}
