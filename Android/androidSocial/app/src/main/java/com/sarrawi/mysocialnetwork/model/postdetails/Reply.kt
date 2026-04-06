package com.sarrawi.mysocialnetwork.model.postdetails

//data class Reply(
//    val id: Int,
//    val comment: String?,
//    val image: String?,
//    val image_url: String?,
//    val author: CommentAuthor,
//    val created_on: String,
//    val likes_count: Int,
//    val dislikes_count: Int,
//    val replies: List<Reply> // ردود داخل الرد
//)

data class Reply(
    val id: Int,
    val comment: String?,
    val image: String?,
    val image_url: String?,
    val author: CommentAuthor,
    val created_on: String,
    val likes_count: Int,
    val dislikes_count: Int,
    val is_liked_by_user: Boolean,
    val is_disliked_by_user: Boolean,
    val replies: List<Reply> = emptyList(),
    val parentCommentId: Int? = null // 👈 هنا نضيف parentCommentId

)


