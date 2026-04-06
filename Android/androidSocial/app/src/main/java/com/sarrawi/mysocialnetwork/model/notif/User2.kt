package com.sarrawi.mysocialnetwork.model.notif

data class User2(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val email: String,
    val profile_picture: String?,
    val followers_count: Int
)
