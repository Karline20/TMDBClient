package coding.legaspi.tmdbclient.presentation.di.core

import android.content.Context
import coding.legaspi.tmdbclient.presentation.di.events.EventSubComponent
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(subcomponents = [
    EventSubComponent::class
])
class AppModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideApplicationContext(): Context{
        return context.applicationContext
    }
}