package com.sarrawi.mysocialnetwork.model

import com.google.gson.annotations.SerializedName

data class PostResponse(
    val id: Int,
    val author: User,

    val body: String?,
    val shared_body: String?,
    val created_on: String,
    val image: List<Image>?,
    val likes_count: Int,
    val dislikes_count: Int,
    val author_image: String?,
    // ✅ أضف هذه الحقول لتتبع تفاعل المستخدم
    @SerializedName("is_liked_by_user")
    var isLikedByUser: Boolean = false,
    @SerializedName("is_disliked_by_user")
    var isDislikedByUser: Boolean = false
)




