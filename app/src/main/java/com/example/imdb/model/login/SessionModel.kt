package com.example.imdb.model.login

import com.google.gson.annotations.SerializedName

data class SessionModel(
    @SerializedName("success") var success: Boolean? = null,
    @SerializedName("session_id") var sessionId: String? = null
)