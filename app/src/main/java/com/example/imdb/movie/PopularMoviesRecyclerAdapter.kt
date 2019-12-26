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
import kotlinx.android.synthetic.main.item_movie_popular.view.*

interface MoviesListener {
    fun onSeriesClick(movies: MovieModel)
}

class PopularMoviesRecyclerAdapter(
    val moviesList: List<MovieModel>,
    val networkService: NetworkService,
    val itemClickListener: MoviesListener?
) :
    RecyclerView.Adapter<PopularMoviesRecyclerAdapter.CustomViewHolder>() {

    private val urlBeginningForImages = BuildConfig.urlBeginningForImages

    override fun getItemCount(): Int {
        return moviesList.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layaoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layaoutInflater.inflate(R.layout.item_movie_popular, parent, false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        val movie = moviesList.get(position)

        movie.id?.let { movieId ->
            if (movie.runtime == 0) {
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

        val radius = holder.view.context?.resources?.getDimensionPixelSize(R.dimen.radius_50) ?: 0
        val margin = 0
        val transformation: Transformation = RoundedCornersTransformation(
            radius,
            margin,
            RoundedCornersTransformation.CornerType.LEFT
        )
        val popularMoviePoster = holder.view.popularMovieImageView
        val imageUrl = urlBeginningForImages + movie.posterPath
        Picasso.with(holder.view.context).load(imageUrl).transform(transformation)
            .into(popularMoviePoster)

        with(holder.view) {
            popularMovieTitleTextView?.text = movie.title
            popularMovieCategoryTextView.setText(movie.genreString)
            popularMovieRuntimeTextView.text = movie.runtime.toString()
            popularMovieReleaseDateTextView?.text = movie.releaseDate
            popularMoviesVoteAverageTextView?.text = movie.voteAverage.toString()
            popularMovieProgressBar.visibility =
                if (popularMovieCategoryTextView.text.isEmpty()) View.VISIBLE else View.INVISIBLE
        }

        holder.view.constraintLayout.setOnClickListener {
            itemClickListener?.onSeriesClick(movie)
        }
    }

    inner class CustomViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}

