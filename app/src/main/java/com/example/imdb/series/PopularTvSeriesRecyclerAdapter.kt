package com.example.imdb.series

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
import com.example.imdb.model.series.TvSerieModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import kotlinx.android.synthetic.main.item_series_popular.view.*

interface TvSeriesListener {
    fun onSeriesClick(series: TvSerieModel)
}

class PopularTvSeriesRecyclerAdapter(
    val tvSeriesList: List<TvSerieModel>,
    val networkService: NetworkService,
    val itemClickListener: TvSeriesListener?
) :
    RecyclerView.Adapter<PopularTvSeriesRecyclerAdapter.CustomViewHolder>() {

    private val urlBeginningForImages = BuildConfig.urlBeginningForImages

    override fun getItemCount() = tvSeriesList.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layaoutInflater = LayoutInflater.from(parent.context)
        val cellForRow =
            layaoutInflater.inflate(R.layout.item_series_popular, parent, false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val tvSerie = tvSeriesList.get(position)
        tvSerie.id?.let { serieId ->
            networkService.getTvSeriesDetail(serieId, object :
                NetworkService.Listener<TvSerieModel> {
                override fun onSuccess(result: TvSerieModel) {
                    holder.view.context.activity()?.run {
                        runOnUiThread {
                            tvSerie.updateDetails(result)
                        }
                    }
                }

                override fun onError(error: ErrorModel) {
                    holder.view.context.activity()?.run { showMessage(error.toString()) }
                }
            })
        }

        val radius = holder.view.context?.resources?.getDimensionPixelSize(R.dimen.radius_10) ?: 0
        val margin = holder.view.context?.resources?.getDimensionPixelSize(R.dimen.margin_5) ?: 0
        val transformation: Transformation =
            RoundedCornersTransformation(radius, margin)
        val popularTvSeriePoster = holder.view.popularTvSeriesImageView
        val imageUrl = urlBeginningForImages + tvSerie.posterPath
        Picasso.with(holder.view.context).load(imageUrl).transform(transformation)
            .into(popularTvSeriePoster)

        holder.view.popularTvSeriesImageView.setOnClickListener {
            itemClickListener?.onSeriesClick(tvSerie)
        }
    }

    inner class CustomViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}