package com.sarrawi.mysocialnetwork.model

data class RegisterRequest(
    val email: String,
    val first_name: String,
    val last_name: String,
    val password1: String,
    val password2: String,
    val birth_day: String,
    val birth_month: String,
    val birth_year: String,
    val gender: String
)

