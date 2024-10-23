package com.example.jaywarehouse.data.common.utils

sealed class BaseResult <out T: Any> {
    data class Success<T:Any>(val data:T?) : BaseResult<T>()
    data class Error<T:Any>(val message: String,val data: T?) : BaseResult<T>()
    data object UnAuthorized: BaseResult<Nothing>()
}