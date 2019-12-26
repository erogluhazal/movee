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
import com.example.imdb.extension.toDateConverter
import com.example.imdb.extension.toDateWithYear
import com.example.imdb.login.LoginFragment
import com.example.imdb.model.cast.CastModel
import com.example.imdb.model.cast.CastResponseModel
import com.example.imdb.model.favorite.FavoriteRequestModel
import com.example.imdb.model.favorite.FavoriteResponseModel
import com.example.imdb.model.favorite.FavoriteStateModel
import com.example.imdb.model.series.TvSerieModel
import com.example.imdb.profile.ProfileFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_tv_series_detail.*
import kotlinx.android.synthetic.main.fragment_tv_series_detail.view.*

class TvSeriesDetailFragment : Fragment(), CreditsListener {

    val networkService: NetworkService by lazy { NetworkServiceImpl() }
    val favoriteRequestModel = FavoriteRequestModel()

    companion object {
        fun newInstance(tvSeriesDetailsModel: TvSerieModel): TvSeriesDetailFragment {
            val args = Bundle()
            args.putParcelable(MODEL, tvSeriesDetailsModel)
            val fragment = TvSeriesDetailFragment()
            fragment.arguments = args
            return fragment
        }

        const val TV_MEDIA_TYPE = "tv"
        const val MODEL = "model"
        const val TVSERIES = "TV Series "
        const val DIRECTOR = "Director"
        const val CREATOR = "Creator: "
        const val CREATORS = "Creators: "
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tv_series_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tvSeriesDetailCastRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var serie = arguments?.getParcelable<TvSerieModel>(MODEL) as TvSerieModel

        if (serie?.genreString != null) {
            with(view) {
                var firstAirDate = serie.firstAirDate?.toDateConverter()
                val convertedFirstAirDate = firstAirDate?.toDateWithYear()
                val lastAirDate = serie.lastAirDate?.toDateConverter()
                val convertedLastAirDate = lastAirDate?.toDateWithYear()
                tvSeriesDetailTitleTextView.text = serie.name
                tvSeriesDetailVoteAverageTextView.text = serie.voteAverage.toString()
                tvSeriesDetailCategoryTextView.text = serie.genreString
                tvSeriesDetailOverviewTextView.text = serie.overview
                var str = serie.episodeRuntime?.get(0).toString()
                tvSeriesDetailRuntimeTextView.text = str
                tvSeriesDetailDateTextView.text =
                    "$TVSERIES($convertedFirstAirDate - $convertedLastAirDate)"
                var seasons = context?.getString(R.string.seasons, serie.numberOfSeasons)
                var season = context.getString(R.string.season, serie.numberOfSeasons)
                if (serie.numberOfSeasons == 1) {
                    tvSeriesDetailSeasonsTextView.text = season
                } else if (serie.numberOfSeasons != 0 || serie.numberOfSeasons != null) {
                    tvSeriesDetailSeasonsTextView.text = seasons
                }
                val imageUrl = BuildConfig.urlBeginningForImages + serie.backdropPath
                Picasso.with(context).load(imageUrl).into(tvSeriesDetailPosterImageView)

                scrollView.setOnClickListener {
                    var fragmentManager = this@TvSeriesDetailFragment.fragmentManager
                    val transaction = fragmentManager?.beginTransaction()
                    transaction?.addToBackStack("")
                    transaction?.commitAllowingStateLoss()
                }

            }
        } else {
            serie?.id?.let { serieId ->
                networkService.getTvSeriesDetail(serieId, object :
                    NetworkService.Listener<TvSerieModel> {
                    override fun onSuccess(result: TvSerieModel) {
                        activity?.runOnUiThread {
                            serie = result
                            with(view) {
                                var firstAirDate = serie.firstAirDate?.toDateConverter()
                                val convertedFirstAirDate = firstAirDate?.toDateWithYear()
                                val lastAirDate = serie.lastAirDate?.toDateConverter()
                                val convertedLastAirDate = lastAirDate?.toDateWithYear()
                                tvSeriesDetailTitleTextView.text = serie.name
                                tvSeriesDetailVoteAverageTextView.text =
                                    serie.voteAverage.toString()
                                tvSeriesDetailCategoryTextView.text = serie.genreString
                                tvSeriesDetailOverviewTextView.text = serie.overview
                                var str =
                                    if (serie.episodeRuntime != null) serie.episodeRuntime.toString() else ""
                                tvSeriesDetailRuntimeTextView.text = str
                                tvSeriesDetailDateTextView.text =
                                    "$TVSERIES($convertedFirstAirDate - $convertedLastAirDate)"
                                var seasons =
                                    context?.getString(R.string.seasons, serie.numberOfSeasons)
                                tvSeriesDetailSeasonsTextView.text = seasons
                                val imageUrl =
                                    BuildConfig.urlBeginningForImages + serie.backdropPath
                                Picasso.with(context).load(imageUrl)
                                    .into(tvSeriesDetailPosterImageView)

                                scrollView.setOnClickListener {
                                    var fragmentManager =
                                        this@TvSeriesDetailFragment.fragmentManager
                                    val transaction = fragmentManager?.beginTransaction()
                                    transaction?.addToBackStack("")
                                    transaction?.commitAllowingStateLoss()
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
        }

        var creatorsText: String?
        serie.id?.let { serieId ->
            networkService.getSerieCredits(serieId, object :
                NetworkService.Listener<CastResponseModel> {
                override fun onSuccess(result: CastResponseModel) {
                    activity?.runOnUiThread {
                        result?.cast?.let { serie.updateCast(it) }
                        serie.casts?.let {

                            tvSeriesDetailCastRecyclerView.adapter =
                                DetailCastRecyclerViewAdapter(
                                    it,
                                    networkService,
                                    this@TvSeriesDetailFragment
                                )
                        }
                        if (result.crew != null) {
                            var crewNameList =
                                result.crew.filter { crew -> crew.job == DIRECTOR }
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
                                        tvSeriesDetailCreatorsTextView?.setText(creatorsText)
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
            serie?.id,
            session_id,
            TV_MEDIA_TYPE,
            listener = object : NetworkService.Listener<FavoriteStateModel> {
                override fun onSuccess(result: FavoriteStateModel) {
                    activity?.runOnUiThread {
                        if (result.favorite == true) {
                            favSeriesButton.setBackgroundResource(R.drawable.icon_heart_red)
                        } else {
                            favSeriesButton.setBackgroundResource(R.drawable.icon_heart_white)
                        }
                    }
                }

                override fun onError(error: ErrorModel) {
                    context.showMessage(getString(R.string.error))
                }
            })

        favSeriesButton.setOnClickListener {
            if (favState == false) {
                with(favoriteRequestModel) {
                    favorite = true
                    mediaId = serie?.id
                    mediaType = "tv"
                }
                if (account_id != null && session_id != "") {
                    networkService.postFavoriteItem(
                        account_id,
                        session_id,
                        favoriteRequestModel,
                        listener = object : NetworkService.Listener<FavoriteResponseModel> {
                            override fun onSuccess(result: FavoriteResponseModel) {
                                favSeriesButton.setBackgroundResource(R.drawable.icon_heart_red)
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
                    mediaId = serie?.id
                    mediaType = "tv"
                }
                if (account_id != null && session_id != "") {
                    networkService.postFavoriteItem(
                        account_id,
                        session_id,
                        favoriteRequestModel,
                        listener = object : NetworkService.Listener<FavoriteResponseModel> {
                            override fun onSuccess(result: FavoriteResponseModel) {
                                favSeriesButton.setBackgroundResource(R.drawable.icon_heart_white)
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