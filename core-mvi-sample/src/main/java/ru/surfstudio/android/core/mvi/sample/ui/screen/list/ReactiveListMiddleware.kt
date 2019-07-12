package ru.surfstudio.android.core.mvi.sample.ui.screen.list

import io.reactivex.Observable
import ru.surfstudio.android.core.mvi.sample.domain.datalist.DataList
import ru.surfstudio.android.core.mvi.event.lifecycle.LifecycleStage
import ru.surfstudio.android.core.mvi.sample.ui.base.middleware.BaseMiddleware
import ru.surfstudio.android.core.mvi.sample.ui.base.middleware.BaseMiddlewareDependency
import ru.surfstudio.android.core.mvi.sample.ui.screen.list.event.ReactiveListEvent
import ru.surfstudio.android.core.mvi.sample.ui.screen.list.reactor.ReactiveListStateHolder
import ru.surfstudio.android.core.mvi.ui.middleware.filterIsInstance
import ru.surfstudio.android.dagger.scope.PerScreen
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@PerScreen
class ReactiveListMiddleware @Inject constructor(
        baseMiddlewareDependency: BaseMiddlewareDependency,
        private val sh: ReactiveListStateHolder
) : BaseMiddleware<ReactiveListEvent>(baseMiddlewareDependency) {

    override fun transform(eventStream: Observable<ReactiveListEvent>): Observable<out ReactiveListEvent> {
        val queryChangedObservable = transformQueryEvent(eventStream)
        val flatMap = eventStream.flatMap(::flatMap)
        return merge(queryChangedObservable, flatMap)
    }

    override fun flatMap(event: ReactiveListEvent): Observable<out ReactiveListEvent> = when (event) {
        is ReactiveListEvent.LifecycleChanged -> reactOnLifecycle(event.stage)
        is ReactiveListEvent.Reload -> loadData()
        is ReactiveListEvent.SwipeRefresh -> loadData(isSwr = true)
        is ReactiveListEvent.LoadNextPage -> loadData(sh.state.data.nextPage)
        is ReactiveListEvent.Show -> reactOnShowEvent(event)
        else -> skip()
    }

    private fun transformQueryEvent(
            eventStream: Observable<ReactiveListEvent>
    ): Observable<ReactiveListEvent.QueryChangedDebounced> =
            eventStream
                    .filterIsInstance<ReactiveListEvent.QueryChanged>()
                    .map { it.query }
                    .distinctUntilChanged()
                    .debounce(1000, TimeUnit.MILLISECONDS)
                    .map { ReactiveListEvent.QueryChangedDebounced(it) }

    private fun reactOnLifecycle(stage: LifecycleStage): Observable<out ReactiveListEvent> =
            when (stage) {
                LifecycleStage.CREATE -> loadData()
                else -> skip()
            }

    private fun reactOnShowEvent(event: ReactiveListEvent.Show): Observable<out ReactiveListEvent> =
            when (event) {
                is ReactiveListEvent.Show.LoadNumbers, is ReactiveListEvent.Show.QueryChanged -> {
                    val data = sh.state.data
                    val filtered = data.filter { it.contains(sh.queryChangedEvent.query) }
                    val filteredEvent = ReactiveListEvent.Show.FilterNumbers(filtered)
                    Observable.just(filteredEvent)
                }
                else -> skip()
            }

    private fun loadData(page: Int = 0, isSwr: Boolean = false) = createObservable(page)
            .io()
            .handleError()
            .mapToLoadable(ReactiveListEvent.LoadNumbers(isSwr = isSwr))

    private fun createObservable(page: Int = 0) = Observable.timer(2, TimeUnit.SECONDS).map {
        DataList<String>(
                (1..20).map { (it + page * 20).toString() },
                page,
                20
        )
    }
}