package com.example.imdb

import com.example.imdb.model.BaseRequestModel
import com.example.imdb.model.account.AccountDetailModel
import com.example.imdb.model.cast.CastModel
import com.example.imdb.model.cast.CastResponseModel
import com.example.imdb.model.combine.CombineResponseModel
import com.example.imdb.model.favorite.FavoriteRequestModel
import com.example.imdb.model.favorite.FavoriteResponseModel
import com.example.imdb.model.favorite.FavoriteStateModel
import com.example.imdb.model.favorite.FavoritesModel
import com.example.imdb.model.login.SessionModel
import com.example.imdb.model.login.TokenModel
import com.example.imdb.model.login.UserRequestModel
import com.example.imdb.model.login.UserResponseModel
import com.example.imdb.model.movie.MovieModel
import com.example.imdb.model.movie.MoviesModel
import com.example.imdb.model.search.SearchContainerModel
import com.example.imdb.model.series.TvSerieModel
import com.example.imdb.model.series.TvSeriesModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

data class ErrorModel(val code: Int = 0, val message: String = "")

interface NetworkService {

    interface Listener<T> {
        fun onSuccess(result: T)
        fun onError(error: ErrorModel)
    }

    fun getPopularList(listener: Listener<MoviesModel>)
    fun getOnShowingList(listener: Listener<MoviesModel>)
    fun getMovieDetail(id: Int, listener: Listener<MovieModel>)
    fun getPopularTvSeriesList(listener: Listener<TvSeriesModel>)
    fun getTopRatedTvSeriesList(listener: Listener<TvSeriesModel>)
    fun getTvSeriesDetail(id: Int?, listener: Listener<TvSerieModel>)
    fun getMovieCredits(id: Int?, listener: Listener<CastResponseModel>)
    fun getSerieCredits(id: Int?, listener: Listener<CastResponseModel>)
    fun getCreditDetail(id: Int?, listener: Listener<CastModel>)
    fun getCombineCredits(id: Int?, listener: Listener<CombineResponseModel>)
    fun getSearchItems(key: String, listener: Listener<SearchContainerModel>)
    fun createRequestToken(listener: Listener<TokenModel>)
    fun postLoginDetails(model: UserRequestModel, listener: Listener<UserResponseModel>)
    fun createSession(model: TokenModel, listener: Listener<SessionModel>)
    fun getAccountDetail(sessionId: String, listener: Listener<AccountDetailModel>)
    fun postFavoriteItem(
        accountId: Int,
        sessionId: String,
        model: FavoriteRequestModel,
        listener: Listener<FavoriteResponseModel>
    )

    fun getFavoriteState(
        id: Int?,
        sessionId: String,
        mediaType: String,
        listener: Listener<FavoriteStateModel>
    )

    fun getFavoriteMovies(accountId: Int, sessionId: String, listener: Listener<FavoritesModel>)
    fun getFavoriteSeries(accountId: Int, sessionId: String, listener: Listener<FavoritesModel>)
}

class NetworkServiceImpl : NetworkService {

    private val client: OkHttpClient by lazy { OkHttpClient() }
    val gson: Gson by lazy { GsonBuilder().create() }

    companion object ENDPOINT {

        private val DETAILS_MOVIES = "movie"
        private const val TV = "tv"
        const val MOVIES_POPULAR = "movie/popular"
        const val MOVIES_NOWPLAYING = "movie/now_playing"
        const val SERIES_TOP_RATED = "tv/top_rated"
        const val SERIES_POPULAR = "tv/popular"
        const val CREDITS = "credits"
        const val PERSON = "person"
        const val COMBINED_CREDITS = "combined_credits"
        const val SEARCH_MULTI = "search/multi"
        const val QUERY = "query"
        const val AUTHENTICATION = "authentication/token/new"
        const val AUTHENTICATION_WITH_LOGIN = "authentication/token/validate_with_login"
        const val MEDIA_TYPE_APP_JSON_UTF8 = "application/json; charset=utf-8"
        const val AUTHENTICATION_WITH_SESSION = "authentication/session/new"
        const val ACCOUNT = "account"
        const val SESSION_ID = "session_id"
        const val FAVORITE = "favorite"
        const val ACCOUNT_STATE = "account_states"
        const val MOVIES = "movies"

        val PARAMS_DEFAULT = hashMapOf<String, Any>(
            "language" to "en-US",
            "api_key" to BuildConfig.API_KEY,
            "page" to "1"
        )

        val PARAMS_FOR_POST = hashMapOf<String, Any>(
            "api_key" to BuildConfig.API_KEY
        )
    }

    private inline fun <reified T> requestPost(
        requestString: String,
        requestBody: String,
        listener: NetworkService.Listener<T>
    ) {
        val mediaType = MediaType.parse(MEDIA_TYPE_APP_JSON_UTF8)
        val body = RequestBody.create(mediaType, requestBody)
        val requestForPost = Request.Builder()
            .url(requestString)
            .post(body)
            .build()
        client.newCall(requestForPost).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {

                val body = response?.body()?.string()
                val result = gson.fromJson(body, object : TypeToken<T>() {}.type) as T
                listener.onSuccess(result)
            }

            override fun onFailure(call: Call, e: IOException) {
                listener.onError(ErrorModel(message = e.localizedMessage ?: "unknown error"))
            }
        })
    }

    private inline fun <reified T> request(
        requestString: String,
        listener: NetworkService.Listener<T>
    ) {
        val request = Request.Builder().url(requestString).build()
        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call?, response: Response?) {
                val body = response?.body()?.string()
                val result = gson.fromJson(body, object : TypeToken<T>() {}.type) as T
                listener.onSuccess(result)
            }

            override fun onFailure(call: Call?, e: IOException) {
                listener.onError(ErrorModel(message = e.localizedMessage ?: "unknown error"))
            }
        })
    }

    override fun getPopularList(listener: NetworkService.Listener<MoviesModel>) {
        get(MOVIES_POPULAR, hashMapOf(), listener)
    }

    override fun getOnShowingList(listener: NetworkService.Listener<MoviesModel>) {
        get(MOVIES_NOWPLAYING, hashMapOf(), listener)
    }

    override fun getMovieDetail(id: Int, listener: NetworkService.Listener<MovieModel>) {
        val endpoint = "$DETAILS_MOVIES/$id"
        get(endpoint, hashMapOf(), listener)
    }

    override fun getPopularTvSeriesList(listener: NetworkService.Listener<TvSeriesModel>) {
        get(SERIES_POPULAR, hashMapOf(), listener)
    }

    override fun getTopRatedTvSeriesList(listener: NetworkService.Listener<TvSeriesModel>) {
        get(SERIES_TOP_RATED, hashMapOf(), listener)
    }

    override fun getTvSeriesDetail(id: Int?, listener: NetworkService.Listener<TvSerieModel>) {
        val endpoint = "$TV/$id"
        get(endpoint, hashMapOf(), listener)
    }

    override fun getMovieCredits(id: Int?, listener: NetworkService.Listener<CastResponseModel>) {
        val endpoint = "$DETAILS_MOVIES/$id/$CREDITS"
        get(endpoint, hashMapOf(), listener)
    }

    override fun getSerieCredits(id: Int?, listener: NetworkService.Listener<CastResponseModel>) {
        val endpoint = "$TV/$id/$CREDITS"
        get(endpoint, hashMapOf(), listener)
    }

    override fun getCreditDetail(id: Int?, listener: NetworkService.Listener<CastModel>) {
        val endpoint = "$PERSON/$id"
        get(endpoint, hashMapOf(), listener)
    }

    override fun getCombineCredits(
        id: Int?,
        listener: NetworkService.Listener<CombineResponseModel>
    ) {
        val endpoint = "$PERSON/$id/$COMBINED_CREDITS"
        get(endpoint, hashMapOf(), listener)
    }

    override fun getSearchItems(
        key: String,
        listener: NetworkService.Listener<SearchContainerModel>
    ) {
        val endpoint = SEARCH_MULTI
        get(endpoint, hashMapOf(QUERY to key), listener)
    }

    override fun createRequestToken(listener: NetworkService.Listener<TokenModel>) {
        get(AUTHENTICATION, hashMapOf(), listener)
    }

    override fun postLoginDetails(
        model: UserRequestModel,
        listener: NetworkService.Listener<UserResponseModel>
    ) {
        post(AUTHENTICATION_WITH_LOGIN, hashMapOf(), model, listener)
    }

    override fun createSession(model: TokenModel, listener: NetworkService.Listener<SessionModel>) {
        post(AUTHENTICATION_WITH_SESSION, hashMapOf(), model, listener)
    }

    override fun getAccountDetail(
        sessionId: String,
        listener: NetworkService.Listener<AccountDetailModel>
    ) {
        val endpoint = "$ACCOUNT"
        get(endpoint, hashMapOf(SESSION_ID to sessionId), listener)
    }

    override fun postFavoriteItem(
        accountId: Int,
        sessionId: String,
        model: FavoriteRequestModel,
        listener: NetworkService.Listener<FavoriteResponseModel>
    ) {
        val endpoint = "$ACCOUNT/$accountId/$FAVORITE"
        post(endpoint, hashMapOf(SESSION_ID to sessionId), model, listener)
    }

    override fun getFavoriteState(
        id: Int?,
        sessionId: String,
        mediaType: String,
        listener: NetworkService.Listener<FavoriteStateModel>
    ) {
        val endpoint = "$mediaType/$id/$ACCOUNT_STATE"
        get(endpoint, hashMapOf(SESSION_ID to sessionId), listener)
    }

    override fun getFavoriteMovies(
        accountId: Int,
        sessionId: String,
        listener: NetworkService.Listener<FavoritesModel>
    ) {
        val endpoint = "$ACCOUNT/$accountId/$FAVORITE/$MOVIES"
        get(endpoint, hashMapOf(SESSION_ID to sessionId), listener)
    }

    override fun getFavoriteSeries(
        accountId: Int,
        sessionId: String,
        listener: NetworkService.Listener<FavoritesModel>
    ) {
        val endpoint = "$ACCOUNT/$accountId/$FAVORITE/$TV"
        get(endpoint, hashMapOf(SESSION_ID to sessionId), listener)
    }

    private inline fun <reified T> get(
        endpoint: String,
        params: HashMap<String, String>,
        listener: NetworkService.Listener<T>
    ) {
        val requestString = createRequestString(endpoint, params)
        request(requestString, listener)
    }

    private inline fun <reified T, reified B : BaseRequestModel> post(
        endpoint: String,
        params: HashMap<String, String>,
        model: B,
        listener: NetworkService.Listener<T>
    ) {
        var requestBody = model.toJson()
        val requestString = createRequestString(endpoint, params)
        requestPost(requestString, requestBody, listener)
    }

    private fun createRequestString(
        endpoint: String,
        params: HashMap<String, *>
    ): String {
        var paramsString = ""
        val allParams = PARAMS_DEFAULT + params
        allParams.forEach {
            paramsString += "${it.key}=${it.value}&"
        }
        return BuildConfig.URL_BASE + endpoint + if (paramsString.isNotEmpty()) "?$paramsString" else ""
    }

}
