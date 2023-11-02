package coding.legaspi.tmdbclient.presentation.di.core

import coding.legaspi.tmdbclient.data.api.TMDBService
import coding.legaspi.tmdbclient.data.repository.events.datasource.AllEventsRemoteDataSource
import coding.legaspi.tmdbclient.data.repository.events.datasourceImpl.AllEventsRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RemoteDataModule {

    @Singleton
    @Provides
    fun provideAllEventRemoteDataSource(tmdbService: TMDBService): AllEventsRemoteDataSource{
        return AllEventsRemoteDataSourceImpl(tmdbService)
    }

}