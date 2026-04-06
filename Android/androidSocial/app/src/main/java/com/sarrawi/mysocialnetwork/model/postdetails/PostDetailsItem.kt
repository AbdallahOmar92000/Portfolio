package com.sarrawi.mysocialnetwork.model.postdetails

import com.sarrawi.mysocialnetwork.model.Post2

sealed class PostDetailsItem {
    data class PostItem(val post: PostDetails) : PostDetailsItem()
    data class CommentItem(val comment: Comment) : PostDetailsItem()
    data class ReplyItem(val reply: Reply, val depth: Int) : PostDetailsItem() // depth للتحكم في المسافة


}
