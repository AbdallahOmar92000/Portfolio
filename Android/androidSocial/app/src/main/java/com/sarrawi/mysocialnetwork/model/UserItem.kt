package com.sarrawi.mysocialnetwork.model

data class UserItem(
    val id: Int,
    val email: String,
    val first_name: String,
    val last_name: String,
    val profile_picture: String?
)