package ru.surfstudio.android.security.sample.ui.screen.free

import ru.surfstudio.android.core.mvp.presenter.BasePresenter
import ru.surfstudio.android.core.mvp.presenter.BasePresenterDependency
import ru.surfstudio.android.dagger.scope.PerScreen
import javax.inject.Inject

/**
 * Презентер экрана, не зависящего от сессии
 */
@PerScreen
class FreePresenter @Inject constructor(basePresenterDependency: BasePresenterDependency
) : BasePresenter<FreeActivityView>(basePresenterDependency)
