package com.sarrawi.mysocialnetwork.model.ProfileModel



data class Profile(
    val user: UserDetails,
    val name: String,
    val bio: String?,
    val picture: String,
    val followers_count: Int,
    val following_count: Int
)