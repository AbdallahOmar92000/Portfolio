package com.sarrawi.mysocialnetwork.model

import com.google.gson.annotations.SerializedName
import com.sarrawi.mysocialnetwork.model.postdetails.Comment

data class Post2(
    val id: Int,
    val body: String?,
    val shared_body: String?,
    val created_on: String?,
    val shared_on: String?,
    val author: Author2,
    val author_image: String?,
    val shared_user: Author2?,
    @SerializedName("image")
    val image: List<Image>?,
    @SerializedName("tags")
    val tags: List<Tag2>?,
    val likes_count: Int,
    val dislikes_count: Int,
    val is_liked_by_user: Boolean,
    val is_disliked_by_user: Boolean

)
