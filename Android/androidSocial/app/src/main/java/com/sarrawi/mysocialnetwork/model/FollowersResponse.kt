package com.sarrawi.mysocialnetwork.model

data class FollowersResponse(val user_id: Int,
                             val followers_count: Int,
                             val followers: List<UserItem>)
