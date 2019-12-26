package com.example.imdb.movie

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
import com.example.imdb.model.movie.MovieModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import kotlinx.android.synthetic.main.item_movie_on_showing.view.*

class OnShowingMoviesRecyclerAdapter(
    val moviesList: List<MovieModel>,
    val networkService: NetworkService,
    val itemClickListener: MoviesListener?
) :
    RecyclerView.Adapter<OnShowingMoviesRecyclerAdapter.CustomViewHolder>() {

    private val urlBeginningForImages = BuildConfig.urlBeginningForImages

    override fun getItemCount() = moviesList.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layaoutInflater = LayoutInflater.from(parent.context)
        val cellForRow =
            layaoutInflater.inflate(R.layout.item_movie_on_showing, parent, false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        val movie = moviesList.get(position)
        if (movie.runtime == 0) {
            movie.id?.let { movieId ->
                networkService.getMovieDetail(movieId, object :
                    NetworkService.Listener<MovieModel> {
                    override fun onSuccess(result: MovieModel) {
                        holder.view.context.activity()?.run {
                            runOnUiThread {
                                movie.updateDetails(result)
                            }
                        }
                    }

                    override fun onError(error: ErrorModel) {
                        holder.view.context.activity()?.run { showMessage(error.toString()) }
                    }
                })
            }
        }

        val radius =
            holder.view.context?.resources?.getDimensionPixelSize(R.dimen.radius_10) ?: 0
        val margin =
            holder.view.context?.resources?.getDimensionPixelSize(R.dimen.margin_5) ?: 0
        val transformation: Transformation =
            RoundedCornersTransformation(radius, margin)
        val onShowingMoviePoster = holder.view.onShowingMovieImageView
        val imageUrl = urlBeginningForImages + movie.posterPath
        Picasso.with(holder.view.context).load(imageUrl).transform(transformation)
            .into(onShowingMoviePoster)

        holder.view.onShowingMovieImageView.setOnClickListener {
            itemClickListener?.onSeriesClick(movie)
        }
    }

    inner class CustomViewHolder(val view: View) : RecyclerView.ViewHolder(view)

}
