package com.sarrawi.mysocialnetwork.model.notif

import com.sarrawi.mysocialnetwork.model.Image


data class PostNot(
    val id: Int,
    val body: String?,
    val shared_body: String?,
    val author: User2,
    val created_on: String,
    val shared_on: String?,
    val likes_count: Int,
    val dislikes_count: Int,
    val comments: List<Comment> = emptyList(),
    val images: List<String> = emptyList()
)

