package com.linari.data.common.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Picture
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.FileProvider
import com.linari.data.auth.models.LoginModel
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import androidx.core.graphics.scale

fun <T:Any> getResult(
    isLogin: Boolean = false,
    request: suspend ()-> Response<T>,
    onSuccess: (T?)->Unit = {},
    onFailure: ((Response<T>)->String)?=null
) : Flow<BaseResult<T>> {
    return flow {
        try {
            val response =  request()

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
                    emit(BaseResult.Error(getErrorMessage(response), response.body()))
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

fun getErrorMessage(response: Response<*>) : String{
    val body = response.errorBody()?.string()
    if (body?.contains("Messages") == true){
        val bodyData = Gson().fromJson(body,ErrorMessages::class.java)
        return bodyData.messages.first()
    } else if(body?.contains("Message") == true){
        val bodyData = Gson().fromJson(body,ErrorMessage::class.java)
        return bodyData.message
    } else {
        return ""
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

fun validatePallet(pallet: String,startString: String = "") : Boolean {
    val palletFields = pallet.trim().split("-")
    if (palletFields.size != 3) return false
    if (startString.isNotEmpty()) {
        if (palletFields[0].uppercase() != startString.uppercase()) return false
    } else {
        if (palletFields[0].length != 2) return false
    }
    if (palletFields[1].length != 6) return false
    if (palletFields[1].any { !it.isDigit() }) return false
    if (palletFields[2].length != 3) return false
    if (palletFields[2].any { !it.isDigit()}) return false
    return true
}

fun createImageUri(context: Context,imageName: String): Uri {
    val imageFile = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "$imageName.jpeg"
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}

fun getFileFromUri(context: Context,uri: Uri): File? {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir,"profile.jpeg")
        file.createNewFile()
        val outputStream = FileOutputStream(file)
        if (inputStream!=null){
            val buffer = ByteArray(4*1024)
            var bytesRead : Int
            while (inputStream.read(buffer).also { bytesRead = it } > 0){
                outputStream.write(buffer,0,bytesRead)
            }
        }
        inputStream?.close()
        outputStream.close()
        return file
    }catch (e: IOException){
        Log.e("jaywarehouse", "getFileFromUri: ", e)
        return null
    }

}

fun compressImageFileToMaxSize(
    inputFile: File,
    maxSizeKB: Int = 200
): File {
    val maxSizeBytes = maxSizeKB * 1024

    var bitmap = BitmapFactory.decodeFile(inputFile.absolutePath)

    // Optional: Resize if very large
    if (bitmap.width > 1280 || bitmap.height > 1280) {
        val aspectRatio = bitmap.width.toFloat() / bitmap.height
        val newWidth = if (aspectRatio > 1) 1280 else (1280 * aspectRatio).toInt()
        val newHeight = if (aspectRatio > 1) (1280 / aspectRatio).toInt() else 1280
        bitmap = bitmap.scale(newWidth, newHeight)
    }

    var quality = 100
    val stream = ByteArrayOutputStream()
    var compressedBytes: ByteArray

    do {
        stream.reset()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        compressedBytes = stream.toByteArray()
        quality -= 10
    } while (compressedBytes.size > maxSizeBytes && quality > 10)

    inputFile.outputStream().use {
        it.write(compressedBytes)
    }

    return inputFile
}
