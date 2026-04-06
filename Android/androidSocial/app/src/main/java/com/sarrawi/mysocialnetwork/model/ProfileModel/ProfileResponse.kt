package com.sarrawi.mysocialnetwork.model.ProfileModel

import com.google.gson.annotations.SerializedName
import com.sarrawi.mysocialnetwork.model.Post2


data class ProfileResponse(
    val user: UserInfo,
    val profile: Profile,
    val posts: List<Post2>,
    val is_following: Boolean

)
