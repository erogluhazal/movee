package com.example.imdb.search

import TvSeriesDetailFragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.imdb.ErrorModel
import com.example.imdb.NetworkService
import com.example.imdb.NetworkServiceImpl
import com.example.imdb.R
import com.example.imdb.cast.CastDetailFragment
import com.example.imdb.extension.closeKeyboard
import com.example.imdb.extension.navigateTo
import com.example.imdb.extension.openKeyboardWithFocus
import com.example.imdb.extension.setOnDrawableEndClick
import com.example.imdb.extension.showMessage
import com.example.imdb.helper.MyDividerItemDecoration
import com.example.imdb.model.cast.CastModel
import com.example.imdb.model.combine.CombineCastModel
import com.example.imdb.model.combine.ItemType
import com.example.imdb.model.movie.MovieModel
import com.example.imdb.model.search.SearchContainerModel
import com.example.imdb.model.series.TvSerieModel
import com.example.imdb.movie.detail.MoviesDetailFragment
import kotlinx.android.synthetic.main.fragment_search.*
import java.util.*

class SearchFragment : Fragment(), TextWatcher, SearchItemClickListener {

    val networkService: NetworkService by lazy { NetworkServiceImpl() }
    val searchPageRecyclerAdapter = SearchPageRecyclerViewAdapter(this@SearchFragment)
    private var timer = Timer()
    private val sizeForDecorator: Int
        get() {
            val res = context?.resources?.getDimensionPixelSize(R.dimen.recycler_10) ?: 0
            return res
        }

    companion object {
        const val DURATION_SEARCH_REQUEST_INTERVAL_MS = 1000L
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        if (menuVisible) {
            searchPageEditText?.openKeyboardWithFocus()
        } else {
            activity?.closeKeyboard()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchPageEditText.addTextChangedListener(this)
        searchListRecyclerView.adapter = searchPageRecyclerAdapter

        context?.let {
            val cardHintPercent = 0.0f
            searchListRecyclerView.addItemDecoration(
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

    override fun afterTextChanged(s: Editable?) {
        val text: String = searchPageEditText.editableText.toString()
        if (text.length >= 3) {
            timer.cancel()
            timer = Timer()
            timer.schedule(
                object : TimerTask() {
                    override fun run() {
                        networkService.getSearchItems(text, object :
                            NetworkService.Listener<SearchContainerModel> {
                            override fun onSuccess(result: SearchContainerModel) {
                                activity?.runOnUiThread {
                                    if (result.results.isNotEmpty()) {
                                        searchPageAnyResultImageView.visibility = View.INVISIBLE
                                        searchPageAnyResultTextView.visibility = View.INVISIBLE
                                    }

                                    searchPageRecyclerAdapter.medias = result.results
                                }
                            }

                            override fun onError(error: ErrorModel) {
                                context.showMessage(error.toString())
                            }
                        })
                    }
                }, DURATION_SEARCH_REQUEST_INTERVAL_MS
            )
        }

        searchPageCancelButton.setOnClickListener {
            clearQueryAndResults()
            activity?.closeKeyboard()
        }

        searchPageEditText.setOnDrawableEndClick(View.OnClickListener {
            clearQueryAndResults()
        })
    }

    private fun clearQueryAndResults() {
        searchPageEditText.text = null
        searchPageRecyclerAdapter.medias = emptyList()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun onItemClick(item: CombineCastModel) {
        navigateToItemDetail(item)
    }

    private fun navigateToItemDetail(cast: CombineCastModel) {
        activity?.closeKeyboard()
        with(cast.id) {
            val layoutId = R.id.tvSeriesDetailLayout
            val targetFragment: Fragment =
                when (cast.itemType) {
                    ItemType.MOVIE -> MoviesDetailFragment.newInstance(MovieModel(this))
                    ItemType.SERIES -> TvSeriesDetailFragment.newInstance(TvSerieModel(this))
                    ItemType.CAST,
                    ItemType.CREW -> CastDetailFragment.newInstance(CastModel(this))
                    ItemType.NONE -> throw UnsupportedOperationException("ne bicim media type :(")
                }

            navigateTo(targetFragment, layoutId)
        }
    }
}