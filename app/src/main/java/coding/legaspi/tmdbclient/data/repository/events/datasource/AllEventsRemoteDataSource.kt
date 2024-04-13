package coding.legaspi.tmdbclient.data.repository.events.datasource

import coding.legaspi.tmdbclient.data.model.auth.EmailVerified
import coding.legaspi.tmdbclient.data.model.auth.EmailVerifiedOutput
import coding.legaspi.tmdbclient.data.model.auth.SignBody
import coding.legaspi.tmdbclient.data.model.auth.SignBodyOutput
import coding.legaspi.tmdbclient.data.model.aboutus.AboutUs
import coding.legaspi.tmdbclient.data.model.aboutus.AboutUsOutput
import coding.legaspi.tmdbclient.data.model.auth.LoginBody
import coding.legaspi.tmdbclient.data.model.auth.LoginBodyOutput
import coding.legaspi.tmdbclient.data.model.addevents.AddEvent
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
import coding.legaspi.tmdbclient.data.model.users.Users
import retrofit2.Response

interface AllEventsRemoteDataSource {

    suspend fun searchEventsByCategory(searchQuery: String, eventCategory: String, category: String): Response<List<AllModelOutput>>

    suspend fun getCategory(category: String): Response<List<AllModelOutput>>

    suspend fun getUsers(): Response<List<Users>>

    suspend fun loginUser(loginBody: LoginBody): Response<LoginBodyOutput>

    suspend fun postEvents(cafe: AllModel): Response<AllModelOutput>

    suspend fun getAllEvents(): Response<List<AllModelOutput>>

    suspend fun delEventsById(id: String): Response<Unit>

    suspend fun patchEventsById(id: String, allModel: AllModel): Response<Unit>

    suspend fun countAllEvents(): Response<count>

    suspend fun getEventsById(id: String): Response<AllModelOutput>

    suspend fun getEventsByCategory(eventcategory: String): Response<List<AllModelOutput>>

    suspend fun countEventsByCategory(eventcategory: String): Response<count>

    suspend fun getAddEvent(): Response<List<AddEvent>>

    suspend fun postRating(rating: Rating): Response<RatingOutput>

    suspend fun averageRating(eventid: String): Response<AverageOutput>

    suspend fun checkExistenceRating(existence: Existence): Response<Unit>

    suspend fun getRating(): Response<List<RatingOutput>>

    suspend fun searchEvents(searchQuery: String): Response<List<AllModelOutput>>

    suspend fun getRatingByEventId(event: String): Response<List<RatingOutput>>

    suspend fun getByUserId(userid: String): Response<ProfileOutput>

    suspend fun getTutorial(): Response<List<Tutorial>>

    suspend fun postTutorialStatus(tutorialStatus: TutorialStatus): Response<TutorialStatusOutput>

    suspend fun getTutorialByUserId(tutorialid: String, userid: String): Response<TutorialStatusOutput>

    suspend fun getTopLeaderBoards():Response<List<RankingOutput>>

    suspend fun postRank(ranking: Ranking): Response<RankingOutput>

    suspend fun checkRanking(userid: String): Response<Unit>

    suspend fun getIdByuserid(userid: String): Response<RankingOutput>

    suspend fun patchRank(id: String, patchRank: PatchRank): Response<Unit>

    //profile

    suspend fun getUserId(userid: String): Response<ProfileOutput>

    suspend fun patchProfile(id: String, profile: Profile): Response<ProfileOutput>

    suspend fun signup(signBody: SignBody): Response<SignBodyOutput>

    suspend fun updateEmailVerified(id: String, emailVerified: EmailVerified): Response<EmailVerified>

    suspend fun isEmailVerified(id: String): Response<EmailVerifiedOutput>

    suspend fun postProfile(profile: Profile): Response<ProfileOutput>

    //favorites

    suspend fun postFavorites(favorites: Favorites): Response<FavoritesOutput>

    suspend fun delFavorites(id: String): Response<Unit>

    suspend fun getFavorites(userid: String): Response<List<FavoritesOutput>>

    suspend fun checkExistenceFavorites(existence: Existence): Response<Boolean>

    //aboutus
    suspend fun postAbout(aboutUs: AboutUs): Response<AboutUsOutput>

    suspend fun getAboutUs(id: String): Response<AboutUsOutput>

    suspend fun patchAboutUs(id: String, aboutUs: AboutUs): Response<AboutUsOutput>

    //terms
    suspend fun postTerms(terms: Terms): Response<TermsOutput>

    suspend fun getTerms(id: String,): Response<TermsOutput>

    suspend fun patchTerms(id: String, terms: Terms): Response<TermsOutput>

    //researcher
    suspend fun postResearch(researchers: Researchers): Response<ResearchersOutput>

    suspend fun getReserachers(): Response<List<ResearchersOutput>>

    suspend fun patchResearchers(id: String, researchers: Researchers): Response<ResearchersOutput>

    suspend fun deleteResearchers(id: String): Response<Unit>

}