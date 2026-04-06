package com.sarrawi.mysocialnetwork.model.postdetails

import com.sarrawi.mysocialnetwork.model.Author2

//data class Comment(
//    val id: Int,
//    val comment: String,
//    val image: String?,
//    val image_url: String?,
//    val author: CommentAuthor,
//    val created_on: String,
//    val likes_count: Int,
//    val dislikes_count: Int,
//    val replies: List<Comment> // نفس النوع → دعم الردود المتداخلة
//)



data class Comment(
    val id: Int,
    val comment: String,
    val image: String?,
    val image_url: String?,
    val author: CommentAuthor,
    val created_on: String,
    val likes_count: Int,
    val dislikes_count: Int,
    val is_liked_by_user: Boolean,
    val is_disliked_by_user: Boolean,
    val replies: List<Comment> = emptyList() // ← تأكد من وجود قيمة افتراضية

)
