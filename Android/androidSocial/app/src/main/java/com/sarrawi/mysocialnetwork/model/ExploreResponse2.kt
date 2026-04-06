package com.sarrawi.mysocialnetwork.model

import com.google.gson.annotations.SerializedName

data class ExploreResponse2(
    @SerializedName("tags")
    val tags: List<Tag2>,
    @SerializedName("posts")
    val posts: List<Post2>,
    @SerializedName("no_results")
    val no_results: Boolean)
