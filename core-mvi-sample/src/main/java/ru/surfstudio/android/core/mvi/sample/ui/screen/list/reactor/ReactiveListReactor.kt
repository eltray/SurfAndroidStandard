package ru.surfstudio.android.core.mvi.sample.ui.screen.list.reactor

import ru.surfstudio.android.core.mvi.event.Event
import ru.surfstudio.android.core.mvi.sample.domain.datalist.DataList
import ru.surfstudio.android.core.mvp.binding.rx.loadable.state.LoadableState
import ru.surfstudio.android.core.mvi.sample.ui.base.holder.BaseStateHolder
import ru.surfstudio.android.core.mvi.sample.ui.screen.list.event.ReactiveListEvent
import ru.surfstudio.android.core.mvi.sample.ui.screen.list.extension.mapDataList
import ru.surfstudio.android.core.mvi.sample.ui.screen.list.extension.mapError
import ru.surfstudio.android.core.mvi.sample.ui.screen.list.extension.mapLoading
import ru.surfstudio.android.core.mvi.ui.effect.SideEffect
import ru.surfstudio.android.core.mvi.ui.reactor.Reactor
import ru.surfstudio.android.core.mvp.binding.rx.relation.mvp.State
import ru.surfstudio.android.dagger.scope.PerScreen
import javax.inject.Inject

@PerScreen
class ReactiveListStateHolder @Inject constructor() : BaseStateHolder<ReactiveListEvent>() {
    val list = LoadableState<DataList<String>>()
    val query = State<String>()
    val filteredList = State<List<String>>()

    override val sideEffects = createEffects {
        add { list.observeData with { ReactiveListEvent.Ui.ShowNumbers(it) } }
        add { query with { ReactiveListEvent.Ui.ShowQuery(it) } }
    }

}

@PerScreen
class ReactiveListReactor @Inject constructor() : Reactor<ReactiveListEvent, ReactiveListStateHolder> {

    override fun react(holder: ReactiveListStateHolder, event: ReactiveListEvent) {
        when (event) {
            is ReactiveListEvent.LoadNumbers -> reactOnListLoadEvent(holder, event)
            is ReactiveListEvent.FilterNumbers -> reactOnFilterEvent(holder)
            is ReactiveListEvent.QueryChangedDebounced -> holder.query.accept(event.query)
        }
    }

    private fun reactOnListLoadEvent(
            holder: ReactiveListStateHolder,
            event: ReactiveListEvent.LoadNumbers
    ) {
        holder.list.modify {
            val hasData = data.hasValue && !data.get().isEmpty()
            copy(
                    data = mapDataList(event.type, data, hasData),
                    load = mapLoading(event.type, hasData, event.isSwr),
                    error = mapError(event.type, hasData)
            )
        }
    }

    private fun reactOnFilterEvent(holder: ReactiveListStateHolder) {
        val data = holder.list.dataOrNull ?: return
        val query = if (holder.query.hasValue) holder.query.value else null
        val list = if (data.isNotEmpty() && !query.isNullOrEmpty()) {
            data.filter { it.contains(query, true) }
        } else {
            data
        }
        holder.filteredList.accept(list)
    }
}


fun <E : Event, H : BaseStateHolder<out E>> H.createEffects(block: Pair<H, MutableList<SideEffect<E, *>>>.() -> Unit) = effects(this, block)

fun <E : Event, H : BaseStateHolder<out E>> effects(holder: H, block: Pair<H, MutableList<SideEffect<E, *>>>.() -> Unit): MutableList<SideEffect<E, *>> {
    val listWithEffects = mutableListOf<SideEffect<E, *>>()
    return (holder to listWithEffects).apply(block).second
}

infix fun <E : Event, L : MutableList<SideEffect<E, *>>, H : BaseStateHolder<out E>> Pair<H, L>.add(block: H.() -> SideEffect<E, *>) =
        this.second.add(this.first.run(block))
