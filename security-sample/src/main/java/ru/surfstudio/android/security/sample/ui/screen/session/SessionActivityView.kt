package ru.surfstudio.android.security.sample.ui.screen.session

import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.LayoutRes
import ru.surfstudio.android.core.mvp.activity.BaseRenderableActivityView
import ru.surfstudio.android.core.mvp.presenter.CorePresenter
import ru.surfstudio.android.logger.Logger
import ru.surfstudio.android.security.sample.R
import ru.surfstudio.android.security.sample.ui.base.configurator.CustomActivityScreenConfigurator
import javax.inject.Inject

/**
 * Вью экрана сессии
 */
class SessionActivityView : BaseRenderableActivityView<SessionScreenModel>() {

    @Inject
    lateinit var presenter: SessionPresenter

    override fun getPresenters(): Array<CorePresenter<*>> = arrayOf(presenter)

    override fun createConfigurator(): CustomActivityScreenConfigurator = SessionScreenConfigurator(intent)

    @LayoutRes
    override fun getContentView(): Int = R.layout.activity_session

    override fun renderInternal(screenModel: SessionScreenModel) { }

    override fun getScreenName(): String = "session"

    override fun onCreate(savedInstanceState: Bundle?,
                          persistentState: PersistableBundle?,
                          viewRecreated: Boolean) {
        super.onCreate(savedInstanceState, persistentState, viewRecreated)
        Logger.d("AAA SessionActivityView onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d("AAA SessionActivityView onCreate")
    }
}
