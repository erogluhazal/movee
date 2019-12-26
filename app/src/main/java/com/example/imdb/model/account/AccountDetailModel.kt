package com.example.imdb.model.account

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AccountDetailModel(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("username") var username: String? = null
)