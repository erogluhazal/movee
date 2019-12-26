package com.example.imdb.model.favorite

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FavoriteStateModel(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("favorite") var favorite: Boolean? = null,
    @SerializedName("rated") var rated: Boolean? = null,
    @SerializedName("watchlist") var watchlist: Boolean? = null
)