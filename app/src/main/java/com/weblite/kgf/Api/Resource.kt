package com.weblite.kgf.Api

import com.weblite.kgf.data.PeriodIdResponse

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}

sealed class PeriodIdUIEvent {
    data class Success(val response: PeriodIdResponse) : PeriodIdUIEvent()
    data class Failure(val msg: String) : PeriodIdUIEvent()
}