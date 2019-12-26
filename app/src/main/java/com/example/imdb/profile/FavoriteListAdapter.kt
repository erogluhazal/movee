package com.example.imdb.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.imdb.BuildConfig
import com.example.imdb.ErrorModel
import com.example.imdb.NetworkService
import com.example.imdb.R
import com.example.imdb.extension.activity
import com.example.imdb.extension.showMessage
import com.example.imdb.helper.RoundedCornersTransformation
import com.example.imdb.login.LoginFragment
import com.example.imdb.model.favorite.FavoriteModel
import com.example.imdb.model.favorite.FavoriteRequestModel
import com.example.imdb.model.favorite.FavoriteResponseModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import kotlinx.android.synthetic.main.item_favorites.view.*

interface FavoriteItemListener {
    fun onItemClick(item: FavoriteModel)
}

class FavoriteListAdapter(
    val favoriteList: List<FavoriteModel>,
    val networkService: NetworkService,
    val itemClickListener: FavoriteItemListener?
) :
    RecyclerView.Adapter<FavoriteListAdapter.CustomViewHolder>() {

    val favoriteRequestModel = FavoriteRequestModel()

    companion object {
        const val TV_MEDIA_TYPE = "tv"
        const val MOVIE_MEDIA_TYPE = "movie"
    }

    private val urlBeginningForImages = BuildConfig.urlBeginningForImages

    override fun getItemCount(): Int {
        return favoriteList.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.item_favorites, parent, false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val favoriteItem = favoriteList.get(position)
        val sharedPref = holder.view.context.getSharedPreferences(
            ProfileFragment.SP_INFO,
            Context.MODE_PRIVATE
        )
        var session_id = sharedPref?.getString(LoginFragment.SESSION_ID, null) ?: ""
        var account_id = sharedPref?.getInt(LoginFragment.ACCOUNT_ID, 0)

        holder.view.favButton.setOnClickListener {
            if (favoriteItem.title != null) {
                if (account_id != null && session_id != "") {

                    with(holder) {
                        Snackbar.make(
                            view,
                            view.context.getString(R.string.snackbar_item_delete),
                            Snackbar.LENGTH_LONG
                        ).addCallback(object : Snackbar.Callback() {
                            override fun onShown(sb: Snackbar?) {
                                super.onShown(sb)
                                it.favButton.setBackgroundResource(R.drawable.icon_heart_white)
                            }

                            override fun onDismissed(
                                transientBottomBar: Snackbar?,
                                event: Int
                            ) {
                                super.onDismissed(transientBottomBar, event)
                                with(favoriteRequestModel) {
                                    favorite = false
                                    mediaId = favoriteItem.id
                                    mediaType = MOVIE_MEDIA_TYPE
                                }
                                networkService.postFavoriteItem(
                                    account_id,
                                    session_id,
                                    favoriteRequestModel,
                                    listener = object :
                                        NetworkService.Listener<FavoriteResponseModel> {
                                        override fun onSuccess(result: FavoriteResponseModel) {
                                            holder.view.context.activity()?.run {
                                                runOnUiThread {
                                                    holder.view.favButton.setBackgroundResource(
                                                        R.drawable.icon_heart_red
                                                    )
                                                    notifyDataSetChanged()
                                                }
                                            }
                                        }

                                        override fun onError(error: ErrorModel) {
                                            holder.view.context.showMessage(
                                                holder.view.context.getString(
                                                    R.string.error
                                                )
                                            )
                                        }
                                    })
                                ProfileFragment.favoriteList.remove(favoriteItem)
                            }
                        })
                            .setAction(view.context.getString(R.string.snackbar_item_delete_undo)) { _ -> }
                        //TODO: snackbar

                    }


                } else {
                    holder.view.context.showMessage(holder.view.context.getString(R.string.error))
                }
            } else {
                with(favoriteRequestModel) {
                    favorite = false
                    mediaId = favoriteItem.id
                    mediaType = TV_MEDIA_TYPE
                }
                if (account_id != null && session_id != "") {
                    networkService.postFavoriteItem(
                        account_id,
                        session_id,
                        favoriteRequestModel,
                        listener = object : NetworkService.Listener<FavoriteResponseModel> {
                            override fun onSuccess(result: FavoriteResponseModel) {
                                holder.view.context.activity()?.run {
                                    runOnUiThread {
                                        it.favButton.setBackgroundResource(R.drawable.icon_heart_white)
                                        ProfileFragment.favoriteList.remove(favoriteItem)
                                        with(holder) {
                                            //TODO: snackbar
                                        }
                                        notifyDataSetChanged()
                                    }
                                }
                            }

                            override fun onError(error: ErrorModel) {
                                holder.view.context.showMessage(holder.view.context.getString(R.string.error))
                            }
                        })
                } else {
                    holder.view.context.showMessage(holder.view.context.getString(R.string.error))
                }
            }
        }

        val radius = holder.view.context?.resources?.getDimensionPixelSize(R.dimen.radius_50) ?: 0
        val margin = 0
        val transformation: Transformation = RoundedCornersTransformation(
            radius,
            margin,
            RoundedCornersTransformation.CornerType.LEFT
        )
        val favoriteItemPoster = holder.view.favoriteItemImageView
        val imageUrl = urlBeginningForImages + favoriteItem.posterPath
        Picasso.with(holder.view.context).load(imageUrl).transform(transformation)
            .into(favoriteItemPoster)

        holder.view.favoriteItemTitleTextView?.text =
            if (favoriteItem.name != null) favoriteItem.name else if (favoriteItem.title != null) favoriteItem.title else ""
        holder.view.favoriteItemImageView.setOnClickListener {
            itemClickListener?.onItemClick(favoriteItem)
        }
    }

    fun snackBarPost(
        holder: CustomViewHolder,
        id: Int?,
        type: String,
        account_id: Int,
        session_id: String
    ) {
        Snackbar.make(
            holder.view,
            holder.view.context.getString(R.string.snackbar_item_delete),
            Snackbar.LENGTH_LONG
        )
            .setAction(holder.view.context.getString(R.string.snackbar_item_delete_undo)) { _ ->
                with(favoriteRequestModel) {
                    favorite = true
                    mediaId = id
                    mediaType = type
                }
                networkService.postFavoriteItem(
                    account_id,
                    session_id,
                    favoriteRequestModel,
                    listener = object :
                        NetworkService.Listener<FavoriteResponseModel> {
                        override fun onSuccess(result: FavoriteResponseModel) {
                            holder.view.context.activity()?.run {
                                runOnUiThread {
                                    holder.view.favButton.setBackgroundResource(
                                        R.drawable.icon_heart_red
                                    )
                                    notifyDataSetChanged()
                                }
                            }
                        }

                        override fun onError(error: ErrorModel) {
                            holder.view.context.showMessage(holder.view.context.getString(R.string.error))
                        }
                    })
            }
            .show()
    }

    inner class CustomViewHolder(val view: View) : RecyclerView.ViewHolder(view)

}