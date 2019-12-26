package com.example.imdb.movie.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imdb.BuildConfig
import com.example.imdb.CreditsListener
import com.example.imdb.DetailCastRecyclerViewAdapter
import com.example.imdb.ErrorModel
import com.example.imdb.NetworkService
import com.example.imdb.NetworkServiceImpl
import com.example.imdb.R
import com.example.imdb.cast.CastDetailFragment
import com.example.imdb.extension.showMessage
import com.example.imdb.login.LoginFragment
import com.example.imdb.model.cast.CastModel
import com.example.imdb.model.cast.CastResponseModel
import com.example.imdb.model.favorite.FavoriteRequestModel
import com.example.imdb.model.favorite.FavoriteResponseModel
import com.example.imdb.model.favorite.FavoriteStateModel
import com.example.imdb.model.movie.MovieModel
import com.example.imdb.profile.ProfileFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_movies_detail.*
import kotlinx.android.synthetic.main.fragment_movies_detail.view.*

class MoviesDetailFragment : Fragment(), CreditsListener {

    val networkService: NetworkService by lazy { NetworkServiceImpl() }
    val favoriteRequestModel = FavoriteRequestModel()

    companion object {
        fun newInstance(movieModel: MovieModel): MoviesDetailFragment {
            val args = Bundle()
            args.putParcelable("model", movieModel)
            val fragment = MoviesDetailFragment()
            fragment.arguments = args
            return fragment
        }

        const val MOVIE_MEDIA_TYPE = "movie"
        const val MODEL = "model"
        const val MOVIE = "Movie "
        const val DIRECTOR = "Director"
        const val CREATOR = "Creator: "
        const val CREATORS = "Creators: "
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movies_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        movieDetailCastRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var movie = arguments?.getParcelable<MovieModel>(MODEL)

        if (movie?.genreString != null) {
            with(view) {
                moviesDetailTitleTextView.text = movie?.title
                moviesDetailRuntimeTextView.text = movie?.runtime.toString()
                moviesDetailVoteAverageTextView.text = movie?.voteAverage.toString()
                moviesDetailCategoryTextView.text = movie?.genreString
                moviesDetailOverviewTextView.text = movie?.overview
                var releaseDate = movie?.releaseDate
                moviesDetailDateTextView.text = "$MOVIE($releaseDate)"
                val imageUrl = BuildConfig.urlBeginningForImages + movie?.backdropPath
                Picasso.with(view.context).load(imageUrl).into(moviesDetailPosterImageView)
            }
        } else {
            movie?.id?.let { movieId ->
                networkService.getMovieDetail(movieId, object :
                    NetworkService.Listener<MovieModel> {
                    override fun onSuccess(result: MovieModel) {
                        activity?.runOnUiThread {
                            movie = result
                            with(view) {
                                moviesDetailTitleTextView.text = movie?.title
                                moviesDetailRuntimeTextView.text = movie?.runtime.toString()
                                moviesDetailVoteAverageTextView.text = movie?.voteAverage.toString()
                                moviesDetailCategoryTextView.text = movie?.genreString
                                moviesDetailOverviewTextView.text = movie?.overview
                                var releaseDate = movie?.releaseDate
                                moviesDetailDateTextView.text = "$MOVIE($releaseDate)"
                                val imageUrl =
                                    BuildConfig.urlBeginningForImages + movie?.backdropPath
                                Picasso.with(view.context).load(imageUrl)
                                    .into(moviesDetailPosterImageView)
                            }
                        }
                    }

                    override fun onError(error: ErrorModel) {
                        activity?.runOnUiThread {
                            context.showMessage(error.toString())
                        }
                    }
                })
            }
        }
        var creatorsText: String?
        movie?.id?.let { movieId ->
            networkService.getMovieCredits(movieId, object :
                NetworkService.Listener<CastResponseModel> {
                override fun onSuccess(result: CastResponseModel) {
                    activity?.runOnUiThread {
                        if (movie != null) {
                            movie?.let { unwrappedMovieModel ->
                                unwrappedMovieModel.updateCast(result.cast)
                                movieDetailCastRecyclerView.adapter =
                                    DetailCastRecyclerViewAdapter(
                                        unwrappedMovieModel.casts,
                                        networkService,
                                        this@MoviesDetailFragment
                                    )
                            }
                        }

                        result.crew.let {
                            var crewNameList =
                                it.filter { crew -> crew.job == DIRECTOR }
                                    .map { crew -> crew.name ?: "" }

                            val arrStr = crewNameList.joinToString(", ")
                            if (arrStr.isNotEmpty()) {
                                with(view) {
                                    result.crewString = arrStr
                                    if (crewNameList.size == 1) {
                                        creatorsText = CREATOR + result.crewString
                                    } else {
                                        creatorsText = CREATORS + result.crewString
                                    }
                                    if (creatorsText != null) {
                                        movieDetailCreatorsTextView?.setText(creatorsText)
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onError(error: ErrorModel) {
                    activity?.runOnUiThread {
                        context.showMessage(error.toString())
                    }
                }
            })
        }

        val sharedPref = context?.getSharedPreferences(
            ProfileFragment.SP_INFO,
            Context.MODE_PRIVATE
        )
        var session_id = sharedPref?.getString(LoginFragment.SESSION_ID, null) ?: ""
        var account_id = sharedPref?.getInt(LoginFragment.ACCOUNT_ID, 0)

        var favState = false

        networkService.getFavoriteState(
            movie?.id,
            session_id,
            MOVIE_MEDIA_TYPE,
            listener = object : NetworkService.Listener<FavoriteStateModel> {
                override fun onSuccess(result: FavoriteStateModel) {
                    activity?.runOnUiThread {
                        if (result.favorite == true) {
                            favMovieButton.setBackgroundResource(R.drawable.icon_heart_red)
                        } else {
                            favMovieButton.setBackgroundResource(R.drawable.icon_heart_white)
                        }
                    }
                }

                override fun onError(error: ErrorModel) {
                    context.showMessage(getString(R.string.error))
                }
            })

        favMovieButton.setOnClickListener {
            if (favState == false) {
                with(favoriteRequestModel) {
                    favorite = true
                    mediaId = movie?.id
                    mediaType = "movie"
                }
                if (account_id != null && session_id != "") {
                    networkService.postFavoriteItem(
                        account_id,
                        session_id,
                        favoriteRequestModel,
                        listener = object : NetworkService.Listener<FavoriteResponseModel> {
                            override fun onSuccess(result: FavoriteResponseModel) {
                                favMovieButton.setBackgroundResource(R.drawable.icon_heart_red)
                                favState = true
                            }

                            override fun onError(error: ErrorModel) {
                                context.showMessage(getString(R.string.error))
                            }
                        })
                } else {
                    context.showMessage(getString(R.string.error))
                }
            } else {
                with(favoriteRequestModel) {
                    favorite = false
                    mediaId = movie?.id
                    mediaType = "movie"
                }
                if (account_id != null && session_id != "") {
                    networkService.postFavoriteItem(
                        account_id,
                        session_id,
                        favoriteRequestModel,
                        listener = object : NetworkService.Listener<FavoriteResponseModel> {
                            override fun onSuccess(result: FavoriteResponseModel) {
                                favMovieButton.setBackgroundResource(R.drawable.icon_heart_white)
                                favState = false
                            }

                            override fun onError(error: ErrorModel) {
                                context.showMessage(getString(R.string.error))
                            }
                        })
                } else {
                    context.showMessage(getString(R.string.error))
                }
            }
        }
    }

    override fun onCreditClick(cast: CastModel) {
        fragmentManager?.beginTransaction()?.run {
            add(R.id.tvSeriesDetailLayout, CastDetailFragment.newInstance(cast))
            addToBackStack("")
            commitAllowingStateLoss()
        }
    }

}
