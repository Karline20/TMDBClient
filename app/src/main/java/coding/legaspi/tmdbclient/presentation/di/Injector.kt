package coding.legaspi.tmdbclient.presentation.di

import coding.legaspi.tmdbclient.presentation.di.events.EventSubComponent

interface Injector {
    fun createEventsSubComponent():EventSubComponent
}

