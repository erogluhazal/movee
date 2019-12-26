package com.example.imdb.model.favorite

import androidx.annotation.Keep
import com.example.imdb.model.BaseRequestModel
import com.google.gson.annotations.SerializedName

@Keep
data class FavoriteRequestModel(
    @SerializedName("media_type") var mediaType: String? = null,
    @SerializedName("media_id") var mediaId: Int? = null,
    @SerializedName("favorite") var favorite: Boolean? = null
) : BaseRequestModel()