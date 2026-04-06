package com.sarrawi.mysocialnetwork.model.notif

import com.google.gson.annotations.SerializedName



data class NotificationResponse(
    val id: Int,
    val notification_type: Int,           // ⚡ من String إلى Int
    val from_user: User2,
    val to_user: User2,
    @SerializedName("post")
    val post: PostNot? = null,            // ⚡ يجب إضافتها
    val comment: Comment? = null,         // ⚡ يجب إضافتها
    val thread: Thread? = null,           // ⚡ يجب إضافتها
    val user_has_seen: Boolean,
    val date: String,
    val message: String
)

