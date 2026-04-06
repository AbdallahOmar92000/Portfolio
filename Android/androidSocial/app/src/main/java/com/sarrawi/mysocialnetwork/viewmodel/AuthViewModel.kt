package com.sarrawi.mysocialnetwork.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarrawi.mysocialnetwork.model.LoginRequest
import com.sarrawi.mysocialnetwork.model.RegisterRequest
import com.sarrawi.mysocialnetwork.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel constructor(private val authRepository: AuthRepository,val context: Context):
    ViewModel() {
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult



    // حالة نجاح التسجيل
    private val _registerResult = MutableLiveData<Boolean>()
    val registerResult: LiveData<Boolean> get() = _registerResult

    // التوكن بعد التسجيل
    private val _token = MutableLiveData<String>()
    val token: LiveData<String> get() = _token

    // تحميل أثناء التسجيل
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading




    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val response = authRepository.login(email, password)
                Log.d("LOGIN", "Token: ${response.key}")
                _token.postValue(response.key)
                prefs.edit().putString("auth_token", response.key).apply()

                _loginResult.postValue(true)
            } catch (e: Exception) {
                Log.e("LOGIN_ERROR", "Exception: ${e.localizedMessage}")
                _loginResult.postValue(false)
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
            } finally {
                prefs.edit().remove("auth_token").apply()
                _loginResult.value = false
            }
        }
    }

    fun getToken(): String? {
        return prefs.getString("auth_token", null)
    }

    // دالة التسجيل
    //postValue تسمح لك بتحديث LiveData
    fun register(email: String,
                 firstName: String,
                 lastName: String,
                 password: String,
                 confirmPassword: String,
                 day: String,
                 month: String,
                 year: String,
                 gender: String) {
        viewModelScope.launch {
            _loading.postValue(true)  // بدء التحميل
            try {
                val response = authRepository.register( email,firstName, lastName, password, confirmPassword, day, month, year, gender)
                Log.d("REGISTER", "Token: ${response.key}")

                _token.postValue(response.key)           // حفظ التوكن
                _registerResult.postValue(true)          // تسجيل ناجح
            } catch (e: Exception) {
                Log.e("REGISTER_ERROR", "Exception: ${e.localizedMessage}")
                _registerResult.postValue(false)         // فشل التسجيل
            } finally {
                _loading.postValue(false)                // إيقاف التحميل
            }
        }
    }

}