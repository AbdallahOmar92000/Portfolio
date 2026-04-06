package com.sarrawi.mysocialnetwork.model.notif

data class Thread(
    val id: Int,
    val sender: User2,
    val receiver: User2,
    val body: String?,
    val image: String?,
    val date: String,
    val is_read: Boolean
)
