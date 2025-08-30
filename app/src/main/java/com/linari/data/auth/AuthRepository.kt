package com.linari.data.auth

import android.os.UserManager
import android.telephony.TelephonyCallback.UserMobileDataStateListener
import com.linari.data.auth.models.AccessPermissionModel
import com.linari.data.auth.models.ChangePasswordModel
import com.linari.data.auth.models.CurrentVersionModel
import com.linari.data.auth.models.DashboardModel
import com.linari.data.auth.models.LoginModel
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.Encryptor
import com.linari.data.common.utils.Prefs
import com.linari.data.common.utils.getResult
import com.google.gson.JsonObject
import com.linari.data.common.utils.ResultMessageModel
import com.linari.data.common.utils.withEnglishDigits
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

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
        jsonObject.addProperty("Username",username.withEnglishDigits())
        jsonObject.addProperty("Password",password.withEnglishDigits())
        return getResult(
            isLogin = true,
            request = {
                api.login(jsonObject)
            },
            onSuccess = {
                prefs.setToken(it?.tokenID?:"")
                prefs.setFullName(it?.fullName?:"")
                prefs.setAccessPermission(
                    accessPermissionModel = AccessPermissionModel(
                        hasRS = it?.hasRS == true,
                        hasCount = it?.hasCount == true,
                        hasLoading = it?.hasLoading == true,
                        hasPicking = it?.hasPicking == true,
                        hasPutaway = it?.hasPutaway == true,
                        hasChecking = it?.hasChecking == true,
                        hasShipping = it?.hasShipping == true,
                        hasTransfer = it?.hasTransfer == true,
                        hasInventory = it?.hasInventory == true,
                        hasCycleCount = it?.hasCycleCount == true,
                        hasPalletConfirm = it?.hasPalletConfirm == true,
                        hasReturnReceiving = it?.hasReturnReceiving == true,
                        hasPickingBD = it?.hasPickingBD == true,
                        hasWaybill = it?.hasWaybill == true
                    )
                )
                prefs.setWarehouse(it?.warehouse)
                prefs.setHasModifyPick(it?.hasModifyPickQty == true)
                prefs.setHasWaste(it?.hasWaste == true)
                prefs.setProfile(it?.userID?:"")
                prefs.setHasPickCancel(it?.hasPickCancel == true)
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

    fun  changePassword(
        oldPassword: String,
        password: String,
        confirmPassword: String
    ) : Flow<BaseResult<ResultMessageModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("CurrentPassword",oldPassword)
        jsonObject.addProperty("NewPassword",password)
        jsonObject.addProperty("ReNewPassword",confirmPassword)
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

    suspend fun getDashboard(warehouseID:Long) : Flow<BaseResult<DashboardModel>> {
        return getResult(
            request = {
                val jsonObject = JsonObject()
                jsonObject.addProperty("WarehouseID",warehouseID)
                api.getDashboard(jsonObject)
            }
        )
    }

    fun getWarehouses() = getResult(
        request = {
            api.getWarehouses()
        }
    )

    fun uploadFile(
        file: File
    ) = getResult(
        request = {
            api.uploadFile(
                prefs.getAddress()+"home/uploadfile",
                MultipartBody.Part.createFormData("File",file.name,
                    file.asRequestBody(MultipartBody.FORM)
                )
            )
        }
    )

    fun createVehicleTracking(
        latitude: Double,
        longitude: Double,
        speed: Float
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("Latitude",latitude)
            jsonObject.addProperty("Longitude",longitude)
            jsonObject.addProperty("Speed",speed)
            api.createVehicleTracking(jsonObject)
        }
    )
}