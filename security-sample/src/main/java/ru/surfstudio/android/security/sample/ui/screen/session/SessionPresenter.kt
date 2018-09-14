package ru.surfstudio.android.security.sample.ui.screen.session

import ru.surfstudio.android.core.mvp.presenter.BasePresenter
import ru.surfstudio.android.core.mvp.presenter.BasePresenterDependency
import ru.surfstudio.android.core.ui.navigation.activity.navigator.ActivityNavigator
import ru.surfstudio.android.dagger.scope.PerScreen
import ru.surfstudio.android.security.sample.ui.screen.free.FreeActivityRoute
import javax.inject.Inject

/**
 * Презентер экрана сессии
 */
@PerScreen
class SessionPresenter @Inject constructor(basePresenterDependency: BasePresenterDependency,
                                           private val activityNavigator: ActivityNavigator
) : BasePresenter<SessionActivityView>(basePresenterDependency) {

    fun openFreeScreen() {
        activityNavigator.start(FreeActivityRoute())
    }
}
