package com.sarrawi.mysocialnetwork.model

data class PostTags(val tags: List<Tag>,
                    val author_email: String,      // أضف هذا
                    val author_image: String?      // أضف هذا، يمكن أن يكون null )
)