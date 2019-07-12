package ru.surfstudio.android.core.mvp.binding.rx.relation.mvp

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction

class CombineState<R> : State<R>() {

    lateinit var disposable: Disposable

    companion object {

        fun <T1, T2, R> combine(first: State<T1>, second: State<T2>, combineFun: (T1, T2) -> R): CombineState<R> =
                combine(first.getObservable(VIEW), second.getObservable(VIEW), combineFun)

        fun <T1, T2, R> combine(first: State<T1>, second: Observable<T2>, combineFun: (T1, T2) -> R): CombineState<R> =
                combine(first.getObservable(VIEW), second, combineFun)

        fun <T1, T2, R> combine(first: Observable<T1>, second: State<T2>, combineFun: (T1, T2) -> R): CombineState<R> =
                combine(first, second.getObservable(VIEW), combineFun)

        fun <T1, T2, R> combine(first: Observable<T1>, second: Observable<T2>, combineFun: (T1, T2) -> R): CombineState<R> {
            val newState = CombineState<R>()
            newState.disposable = Observable.combineLatest(
                    first,
                    second,
                    BiFunction { t1: T1, t2: T2 -> combineFun(t1, t2) }
            )
                    .subscribe(newState.getConsumer(PRESENTER))
            return newState
        }

    }
}