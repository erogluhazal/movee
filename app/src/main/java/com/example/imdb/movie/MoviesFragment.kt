package com.example.imdb.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.imdb.ErrorModel
import com.example.imdb.NetworkService
import com.example.imdb.NetworkServiceImpl
import com.example.imdb.R
import com.example.imdb.extension.showMessage
import com.example.imdb.helper.MyDividerItemDecoration
import com.example.imdb.model.movie.MovieModel
import com.example.imdb.model.movie.MoviesModel
import com.example.imdb.movie.detail.MoviesDetailFragment
import kotlinx.android.synthetic.main.fragment_movie.*

class MoviesFragment : Fragment(), MoviesListener {

    private val sizeForDecorator: Int
        get() {
            val res = context?.resources?.getDimensionPixelSize(R.dimen.recycler_10) ?: 0
            return res
        }
    val networkService: NetworkService by lazy { NetworkServiceImpl() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        popularMoviesRecyclerView.layoutManager = LinearLayoutManager(context)
        onShowingMoviesRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        networkService.getPopularList(object :
            NetworkService.Listener<MoviesModel> {
            override fun onSuccess(result: MoviesModel) {
                activity?.runOnUiThread {
                    result.results?.let {
                        popularMoviesRecyclerView.adapter =
                            PopularMoviesRecyclerAdapter(
                                it,
                                networkService,
                                this@MoviesFragment
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

        onShowingMoviesRecyclerView.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                updatePreviewMovieDetail()
            }
        })

        networkService.getOnShowingList(object :
            NetworkService.Listener<MoviesModel> {
            override fun onSuccess(result: MoviesModel) {
                activity?.runOnUiThread {
                    result.results?.let {
                        onShowingMoviesRecyclerView.adapter =
                            OnShowingMoviesRecyclerAdapter(
                                it,
                                networkService,
                                this@MoviesFragment
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
            val cardWidthPixels =
                (it.resources?.displayMetrics?.heightPixels ?: 0).times(0.65f).toInt()
            val cardHintPercent = 0.0f
            var top = 0
            var bottom = 0
            popularMoviesRecyclerView.addItemDecoration(
                MyDividerItemDecoration(
                    it,
                    (it.resources?.displayMetrics?.heightPixels ?: 0).times(1f).toInt(),
                    cardHintPercent,
                    sizeForDecorator,
                    sizeForDecorator
                )
            )
            onShowingMoviesRecyclerView.addItemDecoration(
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
        snapHelper.attachToRecyclerView(onShowingMoviesRecyclerView)
    }

    fun updatePreviewMovieDetail() {
        moviesProgressBar.visibility =
            if (onShowingMovieCategoryTextView.text.isEmpty()) View.VISIBLE else View.INVISIBLE
        val index =
            ((onShowingMoviesRecyclerView.layoutManager) as
                    LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        if (index != -1) {
            index.let {
                val movie = (onShowingMoviesRecyclerView.adapter as
                        OnShowingMoviesRecyclerAdapter).moviesList.get(index)
                with(movie) {
                    onShowingMovieTitleTextView.text = title
                    onShowingMovieVoteAverageTextView.text =
                        voteAverage.toString()
                    onShowingMovieCategoryTextView.text = genreString
                }
            }
        }
    }

    override fun onSeriesClick(movies: MovieModel) {
        fragmentManager?.beginTransaction()?.run {
            add(R.id.tvSeriesDetailLayout, MoviesDetailFragment.newInstance(movies))
            addToBackStack("")
            commitAllowingStateLoss()
        }
    }
}
