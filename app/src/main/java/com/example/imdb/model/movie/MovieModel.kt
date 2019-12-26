package com.example.imdb.model.movie

import android.os.Parcel
import android.os.Parcelable
import com.example.imdb.model.cast.CastModel
import com.google.gson.annotations.SerializedName

data class MovieModel(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("poster_path") var posterPath: String? = null,
    @SerializedName("release_date") var releaseDate: String? = null,
    @SerializedName("vote_average") var voteAverage: Double = -1.0,
    @SerializedName("genre_ids") val genreIds: List<Int> = emptyList(),
    @SerializedName("runtime") var runtime: Int = 0,
    @SerializedName("genres") var genres: List<MovieGenresModel> = emptyList(),
    @SerializedName("genreString") var genreString: String? = null,
    @SerializedName("overview") var overview: String? = null,
    @SerializedName("backdrop_path") var backdropPath: String? = null,
    @SerializedName("cast") var casts: MutableList<CastModel> = mutableListOf()

) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        TODO("genreIds"),
        parcel.readInt(),
        TODO("genres"),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        TODO("casts")
    )

    fun updateDetails(movies: MovieModel) {
        movies.genres?.let {
            val genreNameList = it.map { genre -> genre.name }
            val arrStr = genreNameList.joinToString(", ")
            with(this) {
                genreString = arrStr
                runtime = movies.runtime
            }
        }
    }

    fun updateCast(cast: List<CastModel>) {
        cast.let {
            casts = cast.toMutableList()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(title)
        parcel.writeString(posterPath)
        parcel.writeString(releaseDate)
        parcel.writeDouble(voteAverage)
        parcel.writeInt(runtime)
        parcel.writeString(genreString)
        parcel.writeString(overview)
        parcel.writeString(backdropPath)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MovieModel> {
        override fun createFromParcel(parcel: Parcel): MovieModel {
            return MovieModel(parcel)
        }

        override fun newArray(size: Int): Array<MovieModel?> {
            return arrayOfNulls(size)
        }
    }

}