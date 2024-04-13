package coding.legaspi.tmdbclient.presentation.di.core

import coding.legaspi.tmdbclient.data.repository.events.AllEventsRepositoryImpl
import coding.legaspi.tmdbclient.data.repository.events.datasource.AllEventsRemoteDataSource
import coding.legaspi.tmdbclient.domain.repository.AllEventsRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Singleton
    @Provides
    fun provideEventRepository(
        eventsRemoteDataSource: AllEventsRemoteDataSource): AllEventsRepository{
        return AllEventsRepositoryImpl(eventsRemoteDataSource)
    }
}