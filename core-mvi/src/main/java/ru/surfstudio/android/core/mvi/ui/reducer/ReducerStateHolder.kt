package ru.surfstudio.android.core.mvi.ui.reducer

import ru.surfstudio.android.core.mvi.event.Event
import ru.surfstudio.android.core.mvi.ui.reactor.StateHolder

/**
 * Класс, содержащий состояние экрана для [Reducer]
 */
abstract class ReducerStateHolder<S> : StateHolder<Event>() {
    abstract var state: S
}