package coding.legaspi.tmdbclient.presentation.di.events

import coding.legaspi.tmdbclient.domain.getusecase.GetEventsUseCase
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class EventModule {
    @EventScope
    @Provides
    fun provideEventViewModelFactory(
        getEventsUseCase: GetEventsUseCase,
    ): EventViewModelFactory {
        return EventViewModelFactory(getEventsUseCase)
    }

}