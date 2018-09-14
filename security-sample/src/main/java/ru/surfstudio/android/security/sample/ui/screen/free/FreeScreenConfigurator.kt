package ru.surfstudio.android.security.sample.ui.screen.free

import android.content.Intent
import dagger.Component
import dagger.Module
import ru.surfstudio.android.core.mvp.configurator.ScreenComponent
import ru.surfstudio.android.dagger.scope.PerScreen
import ru.surfstudio.android.sample.dagger.ui.base.dagger.screen.DefaultActivityScreenModule
import ru.surfstudio.android.sample.dagger.ui.base.dagger.screen.DefaultCustomScreenModule
import ru.surfstudio.android.security.sample.ui.base.configurator.CustomActivityScreenConfigurator
import ru.surfstudio.android.security.sample.ui.base.dagger.activity.CustomActivityComponent

/**
 * Конфигуратор экрана, не зависящего от сессии
 */
class FreeScreenConfigurator(intent: Intent) : CustomActivityScreenConfigurator(intent) {

    @PerScreen
    @Component(dependencies = [CustomActivityComponent::class],
            modules = [DefaultActivityScreenModule::class, FreeScreenModule::class])
    interface FreeScreenComponent
        : ScreenComponent<FreeActivityView>

    @Module
    internal class FreeScreenModule(route: FreeActivityRoute)
        : DefaultCustomScreenModule<FreeActivityRoute>(route)

    override fun createScreenComponent(customActivityComponent: CustomActivityComponent,
                                       activityScreenModule: DefaultActivityScreenModule,
                                       intent: Intent): ScreenComponent<*> {
        return DaggerFreeScreenConfigurator_FreeScreenComponent.builder()
                .customActivityComponent(customActivityComponent)
                .defaultActivityScreenModule(activityScreenModule)
                .freeScreenModule(FreeScreenModule(FreeActivityRoute()))
                .build()
    }
}
