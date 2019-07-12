package ru.surfstudio.android.core.mvi.sample.ui.screen.list

import android.os.Bundle
import android.os.PersistableBundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import kotlinx.android.synthetic.main.activity_reactive_list.*
import ru.surfstudio.android.core.mvi.event.hub.observeOnly
import ru.surfstudio.android.core.mvi.sample.R
import ru.surfstudio.android.core.mvi.ui.BaseReactActivityView
import ru.surfstudio.android.easyadapter.ItemList
import ru.surfstudio.android.easyadapter.pagination.PaginationState
import ru.surfstudio.android.core.mvi.sample.ui.screen.list.controller.ReactiveListController
import ru.surfstudio.android.core.mvi.sample.ui.screen.list.di.ReactiveListScreenConfigurator
import ru.surfstudio.android.core.mvi.sample.ui.base.hub.BaseEventHub
import ru.surfstudio.android.core.mvi.sample.ui.screen.list.event.ReactiveListEvent
import ru.surfstudio.android.core.mvi.sample.ui.screen.list.extension.observeMainLoading
import ru.surfstudio.android.core.mvi.sample.ui.screen.list.extension.observeSwrLoading
import ru.surfstudio.android.core.mvi.sample.ui.screen.list.reactor.ReactiveListStateHolder
import javax.inject.Inject

class ReactiveListActivityView : BaseReactActivityView() {

    private val adapter = PaginationableAdapter { ReactiveListEvent.LoadNextPage().sendTo(hub) }
    private val controller = ReactiveListController()

    override fun createConfigurator() = ReactiveListScreenConfigurator(intent)

    override fun getScreenName() = "ReactiveListActivityView"

    override fun getContentView(): Int = R.layout.activity_reactive_list

    @Inject
    lateinit var bm: ReactiveListStateHolder

    @Inject
    lateinit var hub: BaseEventHub<ReactiveListEvent>

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?, viewRecreated: Boolean) {
        super.onCreate(savedInstanceState, persistentState, viewRecreated)

        bm bindTo { event ->
            when (event) {
                is ReactiveListEvent.Show.FilterNumbers -> createList(event.data)
                is ReactiveListEvent.Show.QueryChanged -> {
                    reactive_query_tv.text.run { replace(0, length, event.query) }
                }
            }
        }
        reactive_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        reactive_rv.adapter = adapter

        reactive_reload_btn.clicks()
                .send(ReactiveListEvent.Reload(), hub)

        reactive_query_tv.textChanges()
                .skipInitialValue()
                .map { ReactiveListEvent.QueryChanged(it.toString()) }
                .sendTo(hub)

        reactive_swr.setOnRefreshListener { ReactiveListEvent.SwipeRefresh().sendTo(hub) }

        bm.state.observeMainLoading() bindTo { reactive_pb.isVisible = it }
        bm.state.observeSwrLoading() bindTo { reactive_swr.isRefreshing = it }
        bm.queryState bindTo { reactive_query_tv.text.run { replace(0, length, it.getOrNull()) } }
    }

    private fun createList(list: List<String>) {
        adapter.setItems(ItemList.create().addAll(list, controller), PaginationState.READY)
    }
}