package ru.surfstudio.android.security.sample.ui.screen.free

import android.content.Context
import android.content.Intent
import ru.surfstudio.android.core.ui.navigation.activity.route.ActivityRoute

/**
 * Роут экрана, не зависящего от сессии
 */
class FreeActivityRoute : ActivityRoute() {
    override fun prepareIntent(context: Context): Intent {
        return Intent(context, FreeActivityView::class.java)
    }
}
