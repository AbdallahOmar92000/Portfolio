package com.sarrawi.mysocialnetwork.model.notif

data class Comment(
    val id: Int,
    val comment: String,
    val author: User2,
    val created_on: String,
    val likes_count: Int,
    val dislikes_count: Int,
    val replies: List<Comment> = emptyList()
)
