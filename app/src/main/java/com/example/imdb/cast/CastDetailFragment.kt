package com.example.imdb.cast

import TvSeriesDetailFragment
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imdb.BuildConfig
import com.example.imdb.ErrorModel
import com.example.imdb.NetworkService
import com.example.imdb.NetworkServiceImpl
import com.example.imdb.R
import com.example.imdb.extension.activity
import com.example.imdb.extension.showMessage
import com.example.imdb.extension.toDateConverter
import com.example.imdb.extension.toDateWithMonthConverter
import com.example.imdb.helper.MyDividerItemDecoration
import com.example.imdb.model.cast.CastModel
import com.example.imdb.model.combine.CombineCastModel
import com.example.imdb.model.combine.CombineResponseModel
import com.example.imdb.model.movie.MovieModel
import com.example.imdb.model.series.TvSerieModel
import com.example.imdb.movie.detail.MoviesDetailFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_cast_detail.*

class CastDetailFragment : Fragment(), CombineCreditsListener {

    var person: CastModel? = null
    val networkService: NetworkService by lazy { NetworkServiceImpl() }

    companion object {
        const val CAST = "cast"
        const val MOVIE = "movie"
        const val TV = "tv"
        fun newInstance(castModel: CastModel): CastDetailFragment {
            val args = Bundle()
            args.putParcelable(CAST, castModel)
            val fragment = CastDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cast_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        combineCreditsRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    @SuppressLint("StringFormatMatches")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        person = arguments?.getParcelable<CastModel>(CAST) as CastModel

        if (person?.birthday == null){
            networkService.getCreditDetail(person?.id, object :
                NetworkService.Listener<CastModel> {
                override fun onSuccess(result: CastModel) {
                    view.context.activity()?.run {
                        runOnUiThread {
                            person?.updateDetails(result)
                            updateUI()
                        }
                    }
                }

                override fun onError(error: ErrorModel) {
                    view.context.activity()?.run { showMessage(error.toString()) }
                }
            })
        } else {
            updateUI()
        }
        networkService.getCombineCredits(person?.id, object :
            NetworkService.Listener<CombineResponseModel> {
            override fun onSuccess(result: CombineResponseModel) {
                activity?.runOnUiThread {
                    if (result.cast != null){
                        combineCreditsRecyclerView.adapter =
                            KnownForAdapter(
                                result.cast,
                                networkService,
                                this@CastDetailFragment
                            )
                    }
                }
            }

            override fun onError(error: ErrorModel) {
                activity?.runOnUiThread {
                    context.showMessage(error.toString())
                }
            }
        })

        context?.let {
            val cardHintPercent = 0.0f
            combineCreditsRecyclerView.addItemDecoration(
                MyDividerItemDecoration(
                    it,
                    (it.resources?.displayMetrics?.heightPixels ?: 0).times(1f).toInt(),
                    cardHintPercent,
                    sizeForDecorator,
                    sizeForDecorator
                )
            )
        }
    }

    private val sizeForDecorator: Int
        get() {
            val res = context?.resources?.getDimensionPixelSize(R.dimen.recycler_10) ?: 0
            println(res)
            return res
        }

    override fun onCreditClick(cast: CombineCastModel) {
        if (cast.mediaType == MOVIE) {
            fragmentManager?.beginTransaction()?.run {
                val model = MovieModel(cast.id)
                add(R.id.tvSeriesDetailLayout, MoviesDetailFragment.newInstance(model))
                addToBackStack("")
                commitAllowingStateLoss()
            }
        } else if (cast.mediaType == TV) {
            fragmentManager?.beginTransaction()?.run {
                val model = TvSerieModel(cast.id)
                add(R.id.tvSeriesDetailLayout, TvSeriesDetailFragment.newInstance(model))
                addToBackStack("")
                commitAllowingStateLoss()
            }
        }

    }

    fun updateUI() {
        with(view) {
            val date = person?.birthday?.toDateConverter()
            val birthday = date?.toDateWithMonthConverter()

            creditNameTextView.text = person?.name
            creditBiographyTextView.text = person?.biography
            val bornString = if (person?.placeOfBirth != null) {
                context?.getString(R.string.born) + birthday + " in " + person?.placeOfBirth
            } else {
                context?.getString(R.string.born) + birthday
            }
            bornTextView.text = bornString
            val imageUrl = BuildConfig.urlBeginningForImages + person?.profilePath
            Picasso.with(context).load(imageUrl).into(creditPosterImageView)

            seeLessTextView.setOnClickListener {
                if (seeLessTextView.text == context?.getString(R.string.see_full)) {
                    creditBiographyTextView.maxLines = Int.MAX_VALUE
                    seeLessTextView.text = context?.getString(R.string.see_less)
                } else {
                    creditBiographyTextView.maxLines = 4
                    seeLessTextView.text = context?.getString(R.string.see_full)
                }
            }

            scrollView.setOnClickListener {
                var fragmentManager = this@CastDetailFragment.fragmentManager
                val transaction = fragmentManager?.beginTransaction()
                transaction?.addToBackStack("")
                transaction?.commitAllowingStateLoss()
            }
        }
    }
}