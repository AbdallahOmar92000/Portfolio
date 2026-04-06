package com.sarrawi.mysocialnetwork.api

import com.sarrawi.mysocialnetwork.model.LoginRequest
import com.sarrawi.mysocialnetwork.model.LoginResponse
import com.sarrawi.mysocialnetwork.model.RegisterRequest
import com.sarrawi.mysocialnetwork.model.RegisterResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiAuth {

    @POST("api/auth/login/")
    suspend fun login (@Body request: LoginRequest):LoginResponse

    @POST("api/auth/logout/")
    suspend fun logout(): Response<Unit>

    @POST("api/auth/registration/")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    companion object {
        var retrofitService: ApiAuth? = null
        fun provideRetrofitInstance(): ApiAuth {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://networksocial.xyz/social/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(ApiAuth::class.java)
            }
            return retrofitService!!
        }

    }
}