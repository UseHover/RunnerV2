package com.hover.runner.utils

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val msgRes: Int? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class ErrorWithRes<T>(data: Int?) : Resource<T>(data as T)
}