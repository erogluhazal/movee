package com.example.imdb.model.favorite

import androidx.annotation.Keep
import com.example.imdb.model.BaseRequestModel
import com.google.gson.annotations.SerializedName

@Keep
data class FavoriteResponseModel(
    @SerializedName("status_code") var statusCode: Int? = null,
    @SerializedName("status_message") var statusMessage: String? = null
) : BaseRequestModel()