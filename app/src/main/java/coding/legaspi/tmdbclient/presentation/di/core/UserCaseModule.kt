package coding.legaspi.tmdbclient.presentation.di.core

import coding.legaspi.tmdbclient.domain.getusecase.GetEventsUseCase
import coding.legaspi.tmdbclient.domain.repository.AllEventsRepository
import dagger.Module
import dagger.Provides

@Module
class UserCaseModule {
    @Provides
    fun providegetRvEventsUseCase(allEventsRepository: AllEventsRepository): GetEventsUseCase {
        return GetEventsUseCase(allEventsRepository)
    }

}