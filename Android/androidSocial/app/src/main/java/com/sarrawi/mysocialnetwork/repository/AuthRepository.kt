package com.sarrawi.mysocialnetwork.repository

import android.content.Context
import com.sarrawi.mysocialnetwork.api.ApiAuth
import com.sarrawi.mysocialnetwork.model.LoginRequest
import com.sarrawi.mysocialnetwork.model.RegisterRequest

import android.util.Log
import com.sarrawi.mysocialnetwork.model.RegisterResponse

class AuthRepository(private val apiAuth: ApiAuth,private val context: Context) {

    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)


    suspend fun login(email: String, password: String) = try {
        val response = apiAuth.login(LoginRequest(email, password))
        Log.d("AuthRepository", "Login success, token: ${response.key}")
        response
    } catch (e: Exception) {
        Log.e("AuthRepository", "Login failed: ${e.localizedMessage}")
        throw e  // إعادة رمي الخطأ عشان يتعامل معه ViewModel أو caller
    }

    suspend fun register(email: String,
                         firstName: String,
                         lastName: String,
                         password: String,
                         confirmPassword: String,
                         day: String,
                         month: String,
                         year: String,
                         gender: String) = try {
        val response = apiAuth.register(
            RegisterRequest(email, firstName, lastName,
                password, confirmPassword,
                day, month, year, gender)
        )
        Log.d("AuthRepository", "Register success, token: ${response.key}")
        response
    } catch (e: Exception) {
        Log.e("AuthRepository", "Register failed: ${e.localizedMessage}")
        throw e
    }


    suspend fun logout() {
        apiAuth.logout()
    }

}

