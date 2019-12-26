package com.example.imdb.model.combine

import android.os.Parcel
import android.os.Parcelable
import com.example.imdb.model.cast.CastModel
import com.google.gson.annotations.SerializedName

data class CombineCastModel(
    @SerializedName("id") var id: Int?,
    @SerializedName("overview") var overview: String?,
    @SerializedName("media_type") var mediaType: String?,
    @SerializedName("vote_average") var voteAverage: Double?,
    @SerializedName("poster_path") var posterPath: String?,
    @SerializedName("backdrop_path") var backdropPath: String?,
    @SerializedName("genre_ids") var genreIds: List<Int>?,
    @SerializedName("cast") var casts: MutableList<CastModel>? = mutableListOf(),
    @SerializedName("genreString") var genreString: String?,
    @SerializedName("character") var character: String?,
    @SerializedName("creditsString") var creditsString: String?,

    //for movies
    @SerializedName("title") var title: String?,
    @SerializedName("release_date") var releaseDate: String?,
    @SerializedName("runtime") var runtime: Double?,

    //for series
    @SerializedName("name") var name: String?, //people have it
    @SerializedName("first_air_date") var firstAirDate: String?,
    @SerializedName("last_air_date") var lastAirDate: String?,
    @SerializedName("episode_run_time") var episodeRuntime: List<Int>?,

    //for peoples
    @SerializedName("profile_path") var profilePath: String?,
    @SerializedName("known_for_department") var knownForDepartment: String?
) : Parcelable {

    val itemType: ItemType
        get() {
            return when (mediaType) {
                "person" -> {
                    profilePath?.let { ItemType.CAST } ?: ItemType.CREW
                }
                else -> {
                    when (mediaType) {
                        "movie" -> ItemType.MOVIE
                        "tv" -> ItemType.SERIES
                        else -> ItemType.NONE
                    }
                }
            }
        }

    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        TODO("genreIds"),
        TODO("casts"),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        TODO("episodeRuntime"),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(overview)
        parcel.writeString(mediaType)
        parcel.writeValue(voteAverage)
        parcel.writeString(posterPath)
        parcel.writeString(backdropPath)
        parcel.writeString(genreString)
        parcel.writeString(character)
        parcel.writeString(creditsString)
        parcel.writeString(title)
        parcel.writeString(releaseDate)
        parcel.writeValue(runtime)
        parcel.writeString(name)
        parcel.writeString(firstAirDate)
        parcel.writeString(lastAirDate)
        parcel.writeString(profilePath)
        parcel.writeString(knownForDepartment)
    }

    fun updateCast(cast: List<CastModel>) {
        cast.let {
            casts = cast.toMutableList()
        }
    }

    fun updateDetails(credits: CombineCastModel) {
        credits.casts?.let {
            val creditsNameList = it.map { credit -> credit.name }
            val arrStr = creditsNameList.joinToString(", ")
            with(this) {
                creditsString = arrStr
            }
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CombineCastModel> {
        override fun createFromParcel(parcel: Parcel): CombineCastModel {
            return CombineCastModel(parcel)
        }

        override fun newArray(size: Int): Array<CombineCastModel?> {
            return arrayOfNulls(size)
        }
    }
}

enum class ItemType(val value: Int) {
    MOVIE(0),
    SERIES(1),
    CAST(2),
    CREW(3),
    NONE(4)
}



