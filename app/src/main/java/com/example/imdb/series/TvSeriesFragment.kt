package com.example.imdb.series

import TvSeriesDetailFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.imdb.ErrorModel
import com.example.imdb.NetworkService
import com.example.imdb.NetworkServiceImpl
import com.example.imdb.R
import com.example.imdb.extension.showMessage
import com.example.imdb.helper.ItemDecorationForTopRatedSeries
import com.example.imdb.helper.MyDividerItemDecoration
import com.example.imdb.model.series.TvSerieModel
import com.example.imdb.model.series.TvSeriesModel
import kotlinx.android.synthetic.main.fragment_tv_series.*

class TvSeriesFragment : Fragment(), TvSeriesListener {

    private val sizeForDecorator: Int
        get() {
            val res = context?.resources?.getDimensionPixelSize(R.dimen.recycler_10) ?: 0
            return res
        }

    private val sizeForTopRatedRow: Int
        get() {
            val res =
                context?.resources?.getDimensionPixelSize(R.dimen.top_rated_item_decoration) ?: 0
            return res
        }

    val networkService: NetworkService by lazy { NetworkServiceImpl() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tv_series, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        popularTvSeriesRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val manager: LinearLayoutManager =
            GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
        topRatedTvSeriesRecyclerView.layoutManager = manager

        networkService.getTopRatedTvSeriesList(object :
            NetworkService.Listener<TvSeriesModel> {
            override fun onSuccess(result: TvSeriesModel) {
                activity?.runOnUiThread {
                    topRatedTvSeriesRecyclerView.adapter =
                        TopRatedTvSeriesRecyclerAdapter(
                            result.results,
                            networkService,
                            this@TvSeriesFragment
                        )
                }
            }

            override fun onError(error: ErrorModel) {
                activity?.runOnUiThread {
                    context.showMessage(error.toString())
                }
            }
        })

        networkService.getPopularTvSeriesList(object :
            NetworkService.Listener<TvSeriesModel> {
            override fun onSuccess(result: TvSeriesModel) {
                activity?.runOnUiThread {
                    result.results.let {
                        popularTvSeriesRecyclerView.adapter =
                            PopularTvSeriesRecyclerAdapter(
                                result.results,
                                networkService,
                                this@TvSeriesFragment
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

        popularTvSeriesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                updatePreviewTvSeriesDetail()
            }
        })

        context?.let {
            val cardWidthPixels =
                (it.resources?.displayMetrics?.heightPixels ?: 0).times(0.65f).toInt()
            val cardHintPercent = 0.0f
            var top = 0
            var bottom = 0
            topRatedTvSeriesRecyclerView.addItemDecoration(
                ItemDecorationForTopRatedSeries(
                    it,
                    sizeForDecorator,
                    sizeForDecorator,
                    sizeForTopRatedRow,
                    sizeForTopRatedRow
                )
            )
            popularTvSeriesRecyclerView.addItemDecoration(
                MyDividerItemDecoration(
                    it,
                    cardWidthPixels,
                    cardHintPercent,
                    top,
                    bottom
                )
            )
        }

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(popularTvSeriesRecyclerView)
    }

    fun updatePreviewTvSeriesDetail() {
        seriesProgressBar.visibility =
            if (popularTvSeriesCategoryTextView.text.isEmpty()) View.VISIBLE else View.INVISIBLE
        val index =
            ((popularTvSeriesRecyclerView.layoutManager) as
                    LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        if (index != -1) {
            val movie = (popularTvSeriesRecyclerView.adapter as
                    PopularTvSeriesRecyclerAdapter).tvSeriesList.get(index)

            with(movie) {
                popularTvSeriesTitleTextView.text = name
                popularTvSeriesVoteAverageTextView.text =
                    voteAverage.toString()
                popularTvSeriesCategoryTextView.text = genreString
            }
        }
    }

    override fun onSeriesClick(series: TvSerieModel) {
        fragmentManager?.beginTransaction()?.run {
            add(R.id.tvSeriesDetailLayout, TvSeriesDetailFragment.newInstance(series))
            addToBackStack("")
            commitAllowingStateLoss()
        }
    }
}