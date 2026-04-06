package com.sarrawi.mysocialnetwork.apiresu


/**
 * فئة مغلّفة لنتائج استدعاءات API
 * Success: تعني أن الطلب نجح ورجع بيانات
 * Error: تعني أن هناك مشكلة في الشبكة أو السيرفر
 */
sealed class ApiResult<out T> {
    data class Success<out T>(val data: T): ApiResult<T>()
    data class Error(val message: String): ApiResult<Nothing>()
}

