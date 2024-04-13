package coding.legaspi.tmdbclient.presentation

import android.app.Application
import coding.legaspi.tmdbclient.BuildConfig
import coding.legaspi.tmdbclient.presentation.di.Injector
import coding.legaspi.tmdbclient.presentation.di.core.AppComponent
import coding.legaspi.tmdbclient.presentation.di.core.AppModule
import coding.legaspi.tmdbclient.presentation.di.core.DaggerAppComponent
import coding.legaspi.tmdbclient.presentation.di.core.NetModule
import coding.legaspi.tmdbclient.presentation.di.events.EventSubComponent


class App: Application(), Injector {

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(applicationContext))
            .netModule(NetModule(BuildConfig.BASE_URL))
            .build()
    }

    override fun createEventsSubComponent(): EventSubComponent {
        return appComponent.eventSubComponent().create()
    }
}