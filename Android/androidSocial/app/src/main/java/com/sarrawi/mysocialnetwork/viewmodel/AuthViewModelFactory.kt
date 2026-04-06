package com.sarrawi.mysocialnetwork.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sarrawi.mysocialnetwork.repository.AuthRepository
import com.sarrawi.mysocialnetwork.repository.PostRepository

class AuthViewModelFactory(
    private val authRepository: AuthRepository,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class PostViewModelFactory(private val postRepository: PostRepository,
                           private val context: Context): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            return PostViewModel(postRepository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}