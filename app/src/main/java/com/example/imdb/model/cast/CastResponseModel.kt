package com.example.imdb.model.cast

import com.google.gson.annotations.SerializedName

data class CastResponseModel(
    @SerializedName("id") val id: Int,
    @SerializedName("crew") val crew: List<CrewModel>,
    @SerializedName("cast") val cast: List<CastModel>,
    @SerializedName("crewString") var crewString: String? = null
)