package ru.surfstudio.android.core.mvi.event.hub

import io.reactivex.Observable
import io.reactivex.functions.Consumer
import ru.surfstudio.android.core.mvi.event.Event

/**
 * [EventHub] с поддержкой Rx.
 */
interface RxEventHub<T : Event> :
        EventHub<T, Observable<T>>,
        Consumer<T>

inline fun <reified R> RxEventHub<*>.observeOnly(): Observable<R> {
    return this.observe()
            .filter { it is R }
            .map { it as R }
}