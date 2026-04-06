package com.sarrawi.mysocialnetwork.model

data class SuggestionResponse(val id: Int,
                              val email: String,
                              val first_name: String,
                              val last_name: String,
                              val birth_date: String?,
                              val profile_pic: String?,)
