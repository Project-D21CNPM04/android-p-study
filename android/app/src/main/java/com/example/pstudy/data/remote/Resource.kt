package com.example.pstudy.data.remote

sealed class Resource<T>(val data: T? = null, val error: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(error: String) : Resource<T>(null, error)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}