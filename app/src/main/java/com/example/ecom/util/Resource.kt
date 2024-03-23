package com.example.ecom.util

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
){
    class Success<T>(data: T): Resource<T>(data)
    class Error<T>(message: String): Resource<T>(message = "")
    class Loading<T>: Resource<T>()
}
