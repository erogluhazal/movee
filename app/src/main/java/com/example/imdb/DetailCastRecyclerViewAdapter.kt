package com.example.imdb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.imdb.extension.activity
import com.example.imdb.extension.showMessage
import com.example.imdb.helper.RoundedCornersTransformation
import com.example.imdb.model.cast.CastModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import kotlinx.android.synthetic.main.item_cast.view.*

interface CreditsListener {
    fun onCreditClick(cast: CastModel)
}

class DetailCastRecyclerViewAdapter(
    val castList: List<CastModel>,
    val networkService: NetworkService,
    val itemClickListener: CreditsListener?
) :
    RecyclerView.Adapter<DetailCastRecyclerViewAdapter.CustomViewHolder>() {

    private val urlBeginningForImages = BuildConfig.urlBeginningForImages

    override fun getItemCount(): Int {
        return castList.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layaoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layaoutInflater.inflate(R.layout.item_cast, parent, false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val cast = castList.get(position)

        networkService.getCreditDetail(cast.id, object :
            NetworkService.Listener<CastModel> {
            override fun onSuccess(result: CastModel) {
                holder.view.context.activity()?.run {
                    runOnUiThread {
                        cast.updateDetails(result)
                    }
                }
            }

            override fun onError(error: ErrorModel) {
                holder.view.context.activity()?.run { showMessage(error.toString()) }
            }
        })

        with(holder.view) {
            val radius = R.dimen.radius_item_cast_list
            val margin = 0
            val transformation: Transformation =
                RoundedCornersTransformation(radius, margin)
            val castPoster = detailCastPosterImageView
            val imageUrl = urlBeginningForImages + cast.profilePath
            Picasso.with(context).load(imageUrl).transform(transformation)
                .into(castPoster)
            detailCastNameTextView.text = cast.name
        }

        holder.view.detailCastPosterImageView.setOnClickListener {
            itemClickListener?.onCreditClick(cast)
        }
    }

    inner class CustomViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}