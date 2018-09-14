package ru.surfstudio.android.security.sample.ui.screen.free

import android.support.annotation.LayoutRes
import ru.surfstudio.android.core.mvp.activity.BaseRenderableActivityView
import ru.surfstudio.android.core.mvp.presenter.CorePresenter
import ru.surfstudio.android.security.sample.R
import ru.surfstudio.android.security.sample.ui.base.configurator.CustomActivityScreenConfigurator
import ru.surfstudio.android.security.session.SessionFree
import javax.inject.Inject

/**
 * Вью экрана, не зависящего от сессии
 */
class FreeActivityView : BaseRenderableActivityView<FreeScreenModel>(), SessionFree {

    @Inject
    lateinit var presenter: FreePresenter

    override fun getPresenters(): Array<CorePresenter<*>> = arrayOf(presenter)

    override fun createConfigurator(): CustomActivityScreenConfigurator = FreeScreenConfigurator(intent)

    @LayoutRes
    override fun getContentView(): Int = R.layout.activity_free

    override fun renderInternal(screenModel: FreeScreenModel) { }

    override fun getScreenName(): String = "free"
}
