package com.sarrawi.mysocialnetwork.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token")
    val key: String
)
