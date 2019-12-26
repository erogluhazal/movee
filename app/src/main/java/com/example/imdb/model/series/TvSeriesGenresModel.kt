package com.example.imdb.model.series

import android.os.Parcel
import android.os.Parcelable

data class TvSeriesGenresModel(val id: Int, val name: String): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TvSeriesGenresModel> {
        override fun createFromParcel(parcel: Parcel): TvSeriesGenresModel {
            return TvSeriesGenresModel(parcel)
        }

        override fun newArray(size: Int): Array<TvSeriesGenresModel?> {
            return arrayOfNulls(size)
        }
    }
}