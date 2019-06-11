package ru.surfstudio.android.mvp.binding.rx.sample.react

import android.content.Context
import android.content.Intent
import ru.surfstudio.android.core.ui.navigation.activity.route.ActivityRoute

class ReactiveScreenRoute : ActivityRoute() {
    override fun prepareIntent(context: Context?): Intent = Intent(context, ReactiveActivityView::class.java)
}