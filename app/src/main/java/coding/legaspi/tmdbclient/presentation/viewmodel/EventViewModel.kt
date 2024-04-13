package coding.legaspi.tmdbclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import coding.legaspi.tmdbclient.data.model.auth.EmailVerified
import coding.legaspi.tmdbclient.data.model.aboutus.AboutUs
import coding.legaspi.tmdbclient.data.model.aboutus.AboutUsOutput
import coding.legaspi.tmdbclient.data.model.auth.LoginBody
import coding.legaspi.tmdbclient.data.model.auth.LoginBodyOutput
import coding.legaspi.tmdbclient.data.model.addevents.AddEvent
import coding.legaspi.tmdbclient.data.model.auth.SignBody
import coding.legaspi.tmdbclient.data.model.count.count
import coding.legaspi.tmdbclient.data.model.events.AllModel
import coding.legaspi.tmdbclient.data.model.eventsoutput.AllModelOutput
import coding.legaspi.tmdbclient.data.model.favorites.Favorites
import coding.legaspi.tmdbclient.data.model.favorites.FavoritesOutput
import coding.legaspi.tmdbclient.data.model.profile.Profile
import coding.legaspi.tmdbclient.data.model.profile.ProfileOutput
import coding.legaspi.tmdbclient.data.model.ranking.PatchRank
import coding.legaspi.tmdbclient.data.model.ranking.Ranking
import coding.legaspi.tmdbclient.data.model.ranking.RankingOutput
import coding.legaspi.tmdbclient.data.model.rating.AverageOutput
import coding.legaspi.tmdbclient.data.model.rating.Existence
import coding.legaspi.tmdbclient.data.model.rating.Rating
import coding.legaspi.tmdbclient.data.model.rating.RatingOutput
import coding.legaspi.tmdbclient.data.model.researchers.Researchers
import coding.legaspi.tmdbclient.data.model.researchers.ResearchersOutput
import coding.legaspi.tmdbclient.data.model.terms.Terms
import coding.legaspi.tmdbclient.data.model.terms.TermsOutput
import coding.legaspi.tmdbclient.data.model.tutorial.Tutorial
import coding.legaspi.tmdbclient.data.model.tutorial.TutorialStatus
import coding.legaspi.tmdbclient.data.model.tutorial.TutorialStatusOutput
import coding.legaspi.tmdbclient.domain.getusecase.GetEventsUseCase
import kotlinx.coroutines.Dispatchers
import java.io.IOException
import coding.legaspi.tmdbclient.Result

class EventViewModel(private val getEventsUseCase: GetEventsUseCase, ) : ViewModel(){

    fun getCategory(category: String) = liveData {
        val getCategory = getEventsUseCase.getCategory(category)
        emit(getCategory)
    }

    fun getUser() = liveData {
        val userList = getEventsUseCase.getUser()
        emit(userList)
    }
    fun getAddEvents() = liveData {
        val addEventList = getEventsUseCase.execute()
        emit(addEventList)
    }


    fun postEvents(events: AllModel) = liveData {
        val events = getEventsUseCase.postEvents(events)
        emit(events)
    }

    fun delEventsById(id: String) = liveData {
        val delEventsById = getEventsUseCase.delEventsById(id)
        emit(delEventsById)
    }

    //patch
    fun patchEventsById(id: String, allModel: AllModel) = liveData {
        val patchEventsById = getEventsUseCase.patchEventsById(id, allModel)
        emit(patchEventsById)
    }

    fun getEventsByCategory(eventcategory: String) = liveData {
        val getEventsByCategory = getEventsUseCase.getEventsByCategory(eventcategory)
        emit(getEventsByCategory)
    }

    fun countEventsByCategory(eventcategory: String) = liveData {
        val countEventsByCategory = getEventsUseCase.countEventsByCategory(eventcategory)
        emit(countEventsByCategory)
    }

    fun getLoginEventUseCase(loginBody: LoginBody) = liveData(Dispatchers.IO) {

        try {
            val login = getEventsUseCase.getLogin(loginBody)
            if (login.isSuccessful) {
                login.body()?.let {
                    emit(Result.Success(it))
                }
            } else {
                emit(Result.Error(IOException("Error: ${login.code()} ${login.message()}")))
            }
        } catch (ioException: IOException) {
            emit(Result.Error(ioException))
        } catch (exception: Exception) {
            emit(Result.Error(exception))
        }
    }

    fun getAboutUs(id: String) = liveData {
        val getAboutUs = getEventsUseCase.getAboutUs(id)
        emit(getAboutUs)
    }

    fun patchAboutUs(id: String, aboutUs: AboutUs) = liveData {
        val patchAboutUs = getEventsUseCase.patchAboutUs(id, aboutUs)
        emit(patchAboutUs)
    }

    fun getTerms(id: String) = liveData {
        val getTerms = getEventsUseCase.getTerms(id)
        emit(getTerms)
    }

    fun patchTerms(id: String, terms: Terms) = liveData {
        val patchTerms = getEventsUseCase.patchTerms(id, terms)
        emit(patchTerms)
    }

    fun postResearch(researchers: Researchers) = liveData {
        val postResearch = getEventsUseCase.postResearch(researchers)
        emit(postResearch)
    }

    fun getReserachers() = liveData {
        val getReserachers = getEventsUseCase.getReserachers()
        emit(getReserachers)
    }

    fun patchResearchers(
        id: String,
        researchers: Researchers
    ) = liveData {
        val patchResearchers = getEventsUseCase.patchResearchers(id, researchers)
        emit(patchResearchers)
    }

    fun deleteResearchers(id: String) = liveData {
        val deleteResearchers = getEventsUseCase.deleteResearchers(id)
        emit(deleteResearchers)
    }

}