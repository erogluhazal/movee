package com.example.imdb.model.series

import android.os.Parcel
import android.os.Parcelable
import com.example.imdb.model.cast.CastModel
import com.google.gson.annotations.SerializedName

data class TvSerieModel(
    @SerializedName("id")val id: Int? = null,
    @SerializedName("name")val name: String? = null,
    @SerializedName("genres")var genres: List<TvSeriesGenresModel>? = emptyList(),
    @SerializedName("poster_path") val posterPath: String? = null,
    @SerializedName("vote_average") val voteAverage: Double? = null,
    @SerializedName("genre_ids") val genreIds: List<Int>? = emptyList(),
    @SerializedName("number_of_episodes") var numberOfEpisodes: Int? = null,
    @SerializedName("overview") var overview: String? = null,
    @SerializedName("backdrop_path") var backdropPath: String? = null,
    @SerializedName("first_air_date") var firstAirDate: String? = null,
    @SerializedName("number_of_seasons") var numberOfSeasons: Int? = null,
    @SerializedName("last_air_date") var lastAirDate: String? = null,
    @SerializedName("episode_run_time") var episodeRuntime: List<Int>? = emptyList(),
    @SerializedName("genreString")var genreString: String? = null,
    @SerializedName("cast")var casts: MutableList<CastModel> = mutableListOf()
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.createTypedArrayList(TvSeriesGenresModel),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        TODO("genreIds"),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        TODO("episodeRuntime"),
        parcel.readString(),
        TODO("casts")
    )

    fun updateDetails(series: TvSerieModel) {
        series.genres?.let {
            val genreNameList = it.map { genre -> genre.name }
            val arrStr = genreNameList.joinToString(", ")
            with(this) {
                genreString = arrStr
                numberOfSeasons = series.numberOfSeasons
                episodeRuntime = series.episodeRuntime
                lastAirDate = series.lastAirDate
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
        parcel.writeString(name)
        parcel.writeTypedList(genres)
        parcel.writeString(posterPath)
        parcel.writeValue(voteAverage)
        parcel.writeValue(numberOfEpisodes)
        parcel.writeString(overview)
        parcel.writeString(backdropPath)
        parcel.writeString(firstAirDate)
        parcel.writeValue(numberOfSeasons)
        parcel.writeString(lastAirDate)
        parcel.writeString(genreString)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TvSerieModel> {
        override fun createFromParcel(parcel: Parcel): TvSerieModel {
            return TvSerieModel(parcel)
        }

        override fun newArray(size: Int): Array<TvSerieModel?> {
            return arrayOfNulls(size)
        }
    }

}