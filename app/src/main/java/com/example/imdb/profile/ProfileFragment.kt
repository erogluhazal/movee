package com.example.imdb.profile

import TvSeriesDetailFragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imdb.ErrorModel
import com.example.imdb.NetworkService
import com.example.imdb.NetworkServiceImpl
import com.example.imdb.R
import com.example.imdb.extension.navigateTo
import com.example.imdb.extension.showMessage
import com.example.imdb.helper.MyDividerItemDecoration
import com.example.imdb.login.LoginFragment
import com.example.imdb.model.favorite.FavoriteModel
import com.example.imdb.model.favorite.FavoritesModel
import com.example.imdb.model.movie.MovieModel
import com.example.imdb.model.series.TvSerieModel
import com.example.imdb.movie.detail.MoviesDetailFragment
import kotlinx.android.synthetic.main.fragment_profile.*

@Suppress("UNREACHABLE_CODE")
class ProfileFragment : Fragment(), FavoriteItemListener {

    val networkService: NetworkService by lazy { NetworkServiceImpl() }

    private val sizeForDecorator: Int
        get() {
            val res = context?.resources?.getDimensionPixelSize(R.dimen.recycler_10) ?: 0
            return res
        }

    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }

        const val SP_INFO = "SP_INFO"
        const val USERNAME = "username"
        const val LOGIN = "login"

        val favoriteList = mutableListOf<FavoriteModel>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = context?.getSharedPreferences(SP_INFO, Context.MODE_PRIVATE)
        var username = sharedPref?.getString(USERNAME, null) ?: ""
        profilePageNameTextView.text = username
        var session_id = sharedPref?.getString(LoginFragment.SESSION_ID, null) ?: ""
        var account_id = sharedPref?.getInt(LoginFragment.ACCOUNT_ID, 0) ?: 0

        profileLogoutButton.setOnClickListener {
            val editor = sharedPref?.edit()
            editor?.putBoolean(LOGIN, false)
            editor?.remove(USERNAME)
            editor?.apply()
            navigateToLoginPage()
        }

        favoritesRecyclerView.layoutManager = LinearLayoutManager(context)

        networkService.getFavoriteMovies(
            account_id,
            session_id,
            object : NetworkService.Listener<FavoritesModel> {
                override fun onSuccess(result: FavoritesModel) {
                    activity?.runOnUiThread {
                        result.results.let {
                            it?.let {
                                addToList(it)
                            }
                        }
                    }
                }

                override fun onError(error: ErrorModel) {
                    context.showMessage(getString(R.string.error))
                }
            })

        networkService.getFavoriteSeries(
            account_id,
            session_id,
            object : NetworkService.Listener<FavoritesModel> {
                override fun onSuccess(result: FavoritesModel) {
                    activity?.runOnUiThread {
                        result.results.let {
                            it?.let {
                                addToList(it)
                            }
                        }
                    }
                }

                override fun onError(error: ErrorModel) {
                    context.showMessage(getString(R.string.error))
                }
            })

        context?.let {
            val cardHintPercent = 0.0f
            favoritesRecyclerView.addItemDecoration(
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

    private fun addToList(list: List<FavoriteModel>) {
        favoriteList.addAll(list)
        favoritesRecyclerView.adapter =
            FavoriteListAdapter(
                favoriteList,
                networkService,
                this@ProfileFragment
            )
    }

    private fun navigateToLoginPage() {
        fragmentManager?.beginTransaction()?.run {
            replace(R.id.tvSeriesDetailLayout, LoginFragment.newInstance())
            commitAllowingStateLoss()
        }
    }

    override fun onItemClick(item: FavoriteModel) {
        navigateToItemDetail(item)
    }

    private fun navigateToItemDetail(favorite: FavoriteModel) {
        with(favorite.id) {
            val layoutId = R.id.tvSeriesDetailLayout
            val targetFragment: Fragment
            if (favorite.name != null) {
                targetFragment = TvSeriesDetailFragment.newInstance(TvSerieModel(this))
            } else if (favorite.title != null) {
                targetFragment = MoviesDetailFragment.newInstance(MovieModel(this))
            } else {
                targetFragment = throw UnsupportedOperationException("")
            }
            navigateTo(targetFragment, layoutId)
        }
    }

}
