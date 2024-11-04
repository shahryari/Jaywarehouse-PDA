package com.example.jaywarehouse.data.auth

import android.os.UserManager
import android.telephony.TelephonyCallback.UserMobileDataStateListener
import com.example.jaywarehouse.data.auth.models.ChangePasswordModel
import com.example.jaywarehouse.data.auth.models.CurrentVersionModel
import com.example.jaywarehouse.data.auth.models.DashboardModel
import com.example.jaywarehouse.data.auth.models.LoginModel
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Encryptor
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.common.utils.getResult
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class AuthRepository(
    private val api: AuthApi,
    private val prefs: Prefs
) {
    suspend fun login(
        username: String,
        password: String,
        rememberMe: Boolean
    ) : Flow<BaseResult<LoginModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Username",username)
        jsonObject.addProperty("Password",password)
        return getResult(
            isLogin = true,
            request = {
                api.login(jsonObject)
            },
            onSuccess = {
                prefs.setToken(it?.tokenID?:"")
                prefs.setFullName(it?.fullName?:"")
                if (rememberMe){
                    val encryptor = Encryptor.getInstance()
                    prefs.setUserName(username)
                    prefs.setPassword(encryptor.encrypt(password))
                } else {
                    prefs.setUserName("")
                    prefs.setPassword("")
                }
            }
        )
    }

    suspend fun getCurrentUser(
    ) : Flow<BaseResult<LoginModel>>{
        return getResult(
            request = {
                api.getCurrentUser()
            }
        )
    }

    suspend fun  changePassword(
        password: String
    ) : Flow<BaseResult<ChangePasswordModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Password",password)
        return getResult(
            request = {
                api.changePassword(jsonObject)
            }
        )
    }

    suspend fun getCurrentVersionInfo() : Flow<BaseResult<CurrentVersionModel>> {
        return getResult(
            request = {
                api.getCurrentVersionInfo()
            }
        )
    }

    suspend fun getDashboard() : Flow<BaseResult<DashboardModel>> {
        return getResult(
            request = {
                api.getDashboard()
            }
        )
    }
}