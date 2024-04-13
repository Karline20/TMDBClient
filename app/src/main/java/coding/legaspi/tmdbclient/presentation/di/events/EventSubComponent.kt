package coding.legaspi.tmdbclient.presentation.di.events

import coding.legaspi.tmdbclient.presentation.about.AboutActivity
import coding.legaspi.tmdbclient.presentation.about.aboutus.AboutUsActivity
import coding.legaspi.tmdbclient.presentation.about.researcher.AddResearcherActivity
import coding.legaspi.tmdbclient.presentation.about.researcher.ResearcherActivity
import coding.legaspi.tmdbclient.presentation.about.terms.TermsActivity
import coding.legaspi.tmdbclient.presentation.addevent.AddEventActivity
import coding.legaspi.tmdbclient.presentation.events.EventsActivity
import coding.legaspi.tmdbclient.presentation.home.HomeActivity
import coding.legaspi.tmdbclient.presentation.login.LoginActivity
import coding.legaspi.tmdbclient.presentation.menu.MenuActivity
import coding.legaspi.tmdbclient.presentation.rvevent.RvEventActivity
import coding.legaspi.tmdbclient.presentation.users.UsersActivity
import coding.legaspi.tmdbclient.presentation.view.ViewEventActivity
import dagger.Subcomponent

@EventScope
@Subcomponent(
    modules = [
        EventModule::class
    ]
)
interface EventSubComponent {
    fun injectAddEventActivity(addEventActivity: AddEventActivity)
    fun injectViewEventActivity(viewEventActivity: ViewEventActivity)
    fun injectAboutActivity(aboutActivity: AboutActivity)
    fun injectEventActivity(eventsActivity: EventsActivity)
    fun injectRvEventActivity(rvEventActivity: RvEventActivity)
    fun injectMenuActivity(menuActivity: MenuActivity)
    fun injectUserActivity(usersActivity: UsersActivity)
    fun injectHomeActivity(homeActivity: HomeActivity)
    fun inject(loginActivity: LoginActivity)
    fun inject(researcherActivity: ResearcherActivity)
    fun inject(addResearcherActivity: AddResearcherActivity)
    fun inject(termsActivity: TermsActivity)
    fun inject(aboutUsActivity: AboutUsActivity)
    @Subcomponent.Factory
    interface Factory{
        fun create():EventSubComponent
    }
}