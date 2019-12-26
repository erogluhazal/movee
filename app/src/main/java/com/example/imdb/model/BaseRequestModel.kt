package com.example.imdb.model

import androidx.annotation.Keep
import com.google.gson.Gson
import java.io.Serializable

@Keep
open class BaseRequestModel : Serializable {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}