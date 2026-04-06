package com.sarrawi.mysocialnetwork.model.postdetails

import com.sarrawi.mysocialnetwork.model.User

data class CommentAuthor(
    val user: User,        // عندك User جاهز
    val name: String?,
    val bio: String?,
    val picture: String?,
    val followers_count: Int?,
    val following_count: Int?,
    val gender: String?,
    val location: String?
)
