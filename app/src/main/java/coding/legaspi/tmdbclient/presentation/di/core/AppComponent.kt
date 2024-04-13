package coding.legaspi.tmdbclient.presentation.di.core

import coding.legaspi.tmdbclient.presentation.di.events.EventSubComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    NetModule::class,
    UserCaseModule::class,
    RepositoryModule::class,
    RemoteDataModule::class
])
interface AppComponent {
    fun eventSubComponent():EventSubComponent.Factory
}