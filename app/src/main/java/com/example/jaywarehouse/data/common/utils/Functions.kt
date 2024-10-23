package com.example.jaywarehouse.data.common.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.jaywarehouse.data.auth.models.LoginModel
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.Response

fun <T:Any> getResult(
    isLogin: Boolean = false,
    request: suspend ()-> Response<T>,
    onSuccess: (T?)->Unit = {},
    onFailure: ((Response<T>)->String)?=null
) : Flow<BaseResult<T>> {
    return flow {
        try {
            val mutex= Mutex()
            val response = mutex.withLock {
//                Log.i("tradLine_req", "getResult: mutex is running")
                val req = request()
//                Log.i("tradLine_req", "getResult: mutex ended")
                req
            }
            Log.i("jaywarehouse_req", "getResult: ${response.raw().request.url} -> ${response.code()}")
            if (response.isSuccessful) {
                onSuccess(response.body())
                emit(BaseResult.Success(response.body()))
            } else if (response.code() == 401 && !isLogin) {
                emit(BaseResult.UnAuthorized)
            } else {
                if (onFailure != null) {
                    emit(BaseResult.Error(onFailure(response), null))
                } else {
                    emit(BaseResult.Error(response.errorBody()?.string() ?: "", response.body()))
                }
                Log.e(
                    "jaywarehouse",
                    "getResult: ${response.code()} -> ${response.errorBody()?.string()}",
                )
            }
        }catch (e: NoJsonException){
            emit(BaseResult.UnAuthorized)
        }
    }
}

fun hideKeyboard(activity: Activity)
{
    val imm: InputMethodManager =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}
fun hideKeyboard2(activity: Activity) {
    // Check if no view has focus:
    val view: View? = activity.currentFocus
    if (view != null) {
        val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}