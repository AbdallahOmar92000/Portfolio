package com.sarrawi.mysocialnetwork.model

data class LikeDislikeResponse(
    val liked: Boolean?,      // أو disliked: Boolean? حسب العملية
    val disliked: Boolean?,
    val post: PostResponse,
    val post2:Post2
)
