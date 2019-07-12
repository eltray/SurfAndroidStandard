package ru.surfstudio.android.core.mvi.sample.ui.screen.list.reactor

import ru.surfstudio.android.core.mvi.sample.domain.datalist.DataList
import ru.surfstudio.android.core.mvi.loadable.LoadableState
import ru.surfstudio.android.core.mvi.optional.Optional
import ru.surfstudio.android.core.mvi.sample.ui.base.hub.BaseEventHub
import ru.surfstudio.android.core.mvi.sample.ui.screen.list.event.ReactiveListEvent
import ru.surfstudio.android.core.mvi.sample.ui.screen.list.extension.mapDataList
import ru.surfstudio.android.core.mvi.sample.ui.screen.list.extension.mapError
import ru.surfstudio.android.core.mvi.sample.ui.screen.list.extension.mapLoading
import ru.surfstudio.android.core.mvi.ui.reactor.Reactor
import ru.surfstudio.android.core.mvi.ui.reactor.StateHolder
import ru.surfstudio.android.core.mvp.binding.rx.relation.mvp.State
import ru.surfstudio.android.dagger.scope.PerScreen
import javax.inject.Inject

class StateModel {
    var query: String = ""
    var filteredList: List<String> = emptyList()
}

@PerScreen
class ReactiveListStateHolder @Inject constructor() : StateHolder<ReactiveListEvent>() {
    val queryState = State<Optional<String>>(Optional.Empty)
    val state = LoadableState<DataList<String>>()
    var model = StateModel()
}

@PerScreen
class ReactiveListReactor @Inject constructor() : Reactor<ReactiveListEvent, ReactiveListStateHolder> {

    override fun react(holder: ReactiveListStateHolder, event: ReactiveListEvent) {
        when (event) {
            is ReactiveListEvent.LoadNumbers -> reactOnListLoadEvent(holder, event)
            is ReactiveListEvent.QueryChangedDebounced -> reactOnQueryChangedEvent(holder, event)
        }
    }

    private fun reactOnListLoadEvent(holder: ReactiveListStateHolder, event: ReactiveListEvent.LoadNumbers) {
        holder.state.modify {
            val hasData = data.hasValue && !data.get().isEmpty()
            copy(
                    data = mapDataList(event.type, data, hasData),
                    load = mapLoading(event.type, hasData, event.isSwr),
                    error = mapError(event.type, hasData)
            )
        }
        holder.accept(ReactiveListEvent.Show.LoadNumbers(holder.state.dataOrNull ?: return))
    }


    private fun reactOnQueryChangedEvent(holder: ReactiveListStateHolder, event: ReactiveListEvent.QueryChangedDebounced) {
        holder.model.query = event.query
        holder.accept(ReactiveListEvent.Show.QueryChanged(event.query))
    }
}