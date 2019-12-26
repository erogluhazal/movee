package com.example.imdb.cast

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
import com.example.imdb.extension.toDateConverter
import com.example.imdb.extension.toDateWithMonthConverter
import com.example.imdb.helper.RoundedCornersTransformation
import com.example.imdb.model.combine.CombineCastModel
import com.example.imdb.model.series.TvSerieModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import kotlinx.android.synthetic.main.item_known_for.view.*


interface CombineCreditsListener {
    fun onCreditClick(cast: CombineCastModel)
}

class KnownForAdapter(
    val castList: List<CombineCastModel>,
    val networkService: NetworkService,
    val itemClickListener: CombineCreditsListener?
) :
    RecyclerView.Adapter<KnownForAdapter.CustomViewHolder>() {

    companion object{
        const val TV = "tv"
    }
    override fun getItemCount(): Int {
        return castList.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layaoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layaoutInflater.inflate(R.layout.item_known_for, parent, false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val cast = castList.get(position)
        var firstAirDate = cast.firstAirDate?.toDateConverter()
        val convertedFirstAirDate = firstAirDate?.toDateWithMonthConverter()
        val lastAirDate = cast.lastAirDate?.toDateConverter()
        val convertedLastAirDate = lastAirDate?.toDateWithMonthConverter()
        val releaseDate = cast.releaseDate?.toDateConverter()
        val convertedReleaseDate = releaseDate?.toDateWithMonthConverter()

        if ((cast.lastAirDate == "" || cast.lastAirDate == null) && cast.mediaType == TV) {
            cast.id?.let { castId ->
                networkService.getTvSeriesDetail(castId, object :
                    NetworkService.Listener<TvSerieModel> {
                    override fun onSuccess(result: TvSerieModel) {
                        holder.view.context.activity()?.run {
                            runOnUiThread {
                                cast.lastAirDate = result.lastAirDate
                                notifyItemChanged(position)
                            }
                        }
                    }

                    override fun onError(error: ErrorModel) {
                        holder.view.context.activity()?.run { showMessage(error.toString()) }
                    }
                })
            }
        }
        with(holder.view){
            val radius = this.context?.resources?.getDimensionPixelSize(R.dimen.radius_50) ?: 0
            val margin = 0
            val transformation: Transformation = RoundedCornersTransformation(
                radius,
                margin,
                RoundedCornersTransformation.CornerType.LEFT
            )
            val imageUrl = BuildConfig.urlBeginningForImages + cast.posterPath
            Picasso.with(holder.view.context).load(imageUrl).transform(transformation)
                .into(combineCreditsPosterImageView)

            if (cast.mediaType == TV){
                combineCreditsTitleTextView.text = cast.name
                combineCreditsReleaseDateTextView.text = "$convertedFirstAirDate - $convertedLastAirDate"
            }
            else {
                combineCreditsTitleTextView.text = cast.title
                combineCreditsReleaseDateTextView.text = convertedReleaseDate
            }
            combineCreditsCharacterTextView.text = cast.character
        }
        holder.view.combineCreditsRowConstrainLayout.setOnClickListener {
            itemClickListener?.onCreditClick(cast)
        }
    }

    inner class CustomViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}