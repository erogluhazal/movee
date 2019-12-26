package com.example.imdb.model.combine

import com.google.gson.annotations.SerializedName

data class CombineResponseModel(
    @SerializedName("cast") val cast: List<CombineCastModel>
    //@SerializedName("crew") val crew: List<CrewModel>
    )