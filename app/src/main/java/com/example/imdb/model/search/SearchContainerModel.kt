package com.example.imdb.model.search

import android.os.Parcel
import android.os.Parcelable
import com.example.imdb.model.combine.CombineCastModel
import com.google.gson.annotations.SerializedName

data class SearchContainerModel(
    @SerializedName("page") var page: String? = null,
    @SerializedName("total_results") var totalResults: Int? = null,
    @SerializedName("total_pages") var totalPages: Int? = null,
    @SerializedName("results") var results: List<CombineCastModel> = emptyList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        TODO("results")
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(page)
        parcel.writeValue(totalResults)
        parcel.writeValue(totalPages)
        parcel.writeTypedList(results)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchContainerModel> {
        override fun createFromParcel(parcel: Parcel): SearchContainerModel {
            return SearchContainerModel(parcel)
        }

        override fun newArray(size: Int): Array<SearchContainerModel?> {
            return arrayOfNulls(size)
        }
    }
}