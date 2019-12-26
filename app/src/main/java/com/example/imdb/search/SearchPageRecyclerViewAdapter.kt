package com.example.imdb.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.imdb.BuildConfig
import com.example.imdb.ErrorModel
import com.example.imdb.NetworkService
import com.example.imdb.NetworkServiceImpl
import com.example.imdb.R
import com.example.imdb.extension.activity
import com.example.imdb.extension.showMessage
import com.example.imdb.helper.RoundedCornersTransformation
import com.example.imdb.model.cast.CastResponseModel
import com.example.imdb.model.combine.CombineCastModel
import com.example.imdb.model.combine.ItemType
import com.example.imdb.model.series.TvSerieModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation

interface SearchItemClickListener {
    fun onItemClick(item: CombineCastModel)
}

class SearchPageRecyclerViewAdapter(
    val itemClickListener: SearchItemClickListener?
) :
    RecyclerView.Adapter<SearchPageRecyclerViewAdapter.ViewHolder>() {

    val networkService: NetworkService by lazy { NetworkServiceImpl() }
    var medias: List<CombineCastModel> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (ItemType.values()[viewType]) {
            ItemType.SERIES -> {
                val view =
                    layoutInflater.inflate(R.layout.item_search_series, parent, false)
                SerieViewHolder(view)
            }
            ItemType.MOVIE -> {
                val view =
                    layoutInflater.inflate(R.layout.item_search_movie, parent, false)
                MovieViewHolder(view)
            }
            ItemType.CAST -> {
                val view =
                    layoutInflater.inflate(R.layout.item_search_cast, parent, false)
                CastPersonViewHolder(view)
            }
            ItemType.CREW -> {
                val view =
                    layoutInflater.inflate(R.layout.item_search_crew, parent, false)
                CrewViewHolder(view)
            }
            ItemType.NONE -> {
                val view =
                    layoutInflater.inflate(R.layout.item_search_series, parent, false)
                SerieViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return medias.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(medias[position])
    }

    override fun getItemViewType(position: Int): Int {
        return medias[position].itemType.value
    }

    abstract inner class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        init {
            itemView.setOnClickListener { itemClickListener?.onItemClick(medias[adapterPosition]) }
        }

        abstract fun bind(model: CombineCastModel)
    }

    inner class CastPersonViewHolder(view: View) : ViewHolder(view) {
        override fun bind(model: CombineCastModel) {
            val title =
                itemView.findViewById<AppCompatTextView>(R.id.searchPageRecyclerPeopleNameTextView)
            val department =
                itemView.findViewById<AppCompatTextView>(R.id.searchPageRecyclerPeopleDepartmentTextView)
            val imageUrl = BuildConfig.urlBeginningForImages + model.profilePath
            val castPersonImage =
                itemView.findViewById<AppCompatImageView>(R.id.searchPageRecyclerPeopleImageView)
            val rad =
                itemView.context.resources.getDimensionPixelSize(R.dimen.radius_50)
            val margin = 0
            val transformation: Transformation =
                RoundedCornersTransformation(
                    rad,
                    margin,
                    RoundedCornersTransformation.CornerType.LEFT
                )
            title.text = model.name
            department.text = model.knownForDepartment
            Picasso.with(itemView.context).load(imageUrl).transform(transformation)
                .into(castPersonImage)
        }
    }

    inner class MovieViewHolder(view: View) : ViewHolder(view) {
        override fun bind(model: CombineCastModel) {
            var search = medias.get(position)
            networkService.getMovieCredits(search.id, object :
                NetworkService.Listener<CastResponseModel> {
                override fun onSuccess(result: CastResponseModel) {
                    itemView.context.activity()?.run {
                        runOnUiThread {
                            if (result.cast != null) {
                                search.updateCast(result.cast)
                            }
                            search.updateDetails(search)
                            val actors =
                                itemView.findViewById<AppCompatTextView>(R.id.searchPageRecyclerMovieActorsTextView)
                            actors.text = search.creditsString
                        }
                    }
                }

                override fun onError(error: ErrorModel) {
                    itemView.context.activity()?.run { showMessage(error.toString()) }
                }
            })

            val title =
                itemView.findViewById<AppCompatTextView>(R.id.searchPageRecyclerMovieTitleTextView)
            val cast =
                itemView.findViewById<AppCompatTextView>(R.id.searchPageRecyclerMovieActorsTextView)
            val imageUrl = BuildConfig.urlBeginningForImages + model.posterPath
            val movieImage =
                itemView.findViewById<AppCompatImageView>(R.id.searchPageRecyclerMoviePosterImageView)
            val rad =
                itemView.context.resources.getDimensionPixelSize(R.dimen.radius_50)
            val margin = 0
            val transformation: Transformation =
                RoundedCornersTransformation(
                    rad,
                    margin,
                    RoundedCornersTransformation.CornerType.LEFT
                )
            title.text = model.title
            cast.text = model.creditsString
            Picasso.with(itemView.context).load(imageUrl).transform(transformation)
                .into(movieImage)
        }
    }

    inner class SerieViewHolder(view: View) : ViewHolder(view) {
        override fun bind(model: CombineCastModel) {
            var search = medias.get(position)
            networkService.getSerieCredits(search.id, object :
                NetworkService.Listener<CastResponseModel> {
                override fun onSuccess(result: CastResponseModel) {
                    itemView.context.activity()?.run {
                        runOnUiThread {
                            if (result.cast != null) {
                                search.updateCast(result.cast)
                            }
                            search.updateDetails(search)
                            val actors =
                                itemView.findViewById<AppCompatTextView>(R.id.searchPageRecyclerSerieActorsTextView)
                            actors.text = search.creditsString
                        }
                    }
                }

                override fun onError(error: ErrorModel) {
                    itemView.context.activity()?.run { showMessage(error.toString()) }
                }
            })

            model.id?.let { castId ->
                networkService.getTvSeriesDetail(castId, object :
                    NetworkService.Listener<TvSerieModel> {
                    override fun onSuccess(result: TvSerieModel) {
                        itemView.context.activity()?.run {
                            runOnUiThread {
                                model.lastAirDate = result.lastAirDate
                            }
                        }
                    }

                    override fun onError(error: ErrorModel) {
                        itemView.context.showMessage(error.toString())
                    }
                })
            }

            val title =
                itemView.findViewById<AppCompatTextView>(R.id.searchPageRecyclerSerieTitleTextView)
            val cast =
                itemView.findViewById<AppCompatTextView>(R.id.searchPageRecyclerSerieActorsTextView)
            val imageUrl = BuildConfig.urlBeginningForImages + model.posterPath
            val serieImage =
                itemView.findViewById<AppCompatImageView>(R.id.searchPageRecyclerSeriePosterImageView)
            val rad =
                itemView.context.resources.getDimensionPixelSize(R.dimen.radius_50)
            val margin = 0
            val transformation: Transformation =
                RoundedCornersTransformation(
                    rad,
                    margin,
                    RoundedCornersTransformation.CornerType.LEFT
                )
            title.text = model.name
            cast.text = model.creditsString
            Picasso.with(itemView.context).load(imageUrl).transform(transformation)
                .into(serieImage)
        }
    }

    inner class CrewViewHolder(view: View) : ViewHolder(view) {
        override fun bind(model: CombineCastModel) {
            val title =
                itemView.findViewById<AppCompatTextView>(R.id.searchPageRecyclerCrewNameTextView)
            val department =
                itemView.findViewById<AppCompatTextView>(R.id.crewPersonDepartment)
            title.text = model.name
            department.text = model.knownForDepartment
            val imageUrl = BuildConfig.urlBeginningForImages + model.posterPath
            val crewImage =
                itemView.findViewById<AppCompatImageView>(R.id.searchPageRecyclerCrewPosterImageView)
            val rad =
                itemView.context.resources.getDimensionPixelSize(R.dimen.radius_50)
            val margin = 0
            val transformation: Transformation =
                RoundedCornersTransformation(
                    rad,
                    margin,
                    RoundedCornersTransformation.CornerType.LEFT
                )
            Picasso.with(itemView.context).load(imageUrl).transform(transformation)
                .into(crewImage)
        }
    }
}
