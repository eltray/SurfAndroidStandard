package ru.surfstudio.standard.cf_pagination

import ru.surfstudio.android.core.mvp.binding.rx.relation.mvp.Action
import ru.surfstudio.android.core.mvp.binding.rx.relation.mvp.State
import ru.surfstudio.android.datalistlimitoffset.domain.datalist.DataList
import ru.surfstudio.android.easyadapter.pagination.PaginationState

/**
 * Бинд-модель для поддержки пагинации
 *
 * @property data пагинируемые данные
 * @property paginationState текущее состояние пагинации
 * @property loadNextPage действие для загрузки следующей страницы данных
 */
interface PaginationableBindModel<T> {

    val data: State<DataList<T>>
    val paginationState: State<PaginationState>
    val loadNextPage: Action<Unit>
}