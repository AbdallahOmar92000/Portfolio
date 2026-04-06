package com.sarrawi.mysocialnetwork.model

data class FollowingResponse2(
    val user_id: Int,
    val following_count: Int,
    val following: List<UserItem>
)
