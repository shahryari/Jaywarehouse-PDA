package com.linari.data.common.utils

import com.google.gson.Gson
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


//string encryptor
class Encryptor private constructor(){

    private val secret = SecretKeySpec("1234567890123456".toByteArray(),"AES")
    private val iv = IvParameterSpec(ByteArray(16))
    companion object{
        @Volatile
        private var instance: Encryptor? = null

        fun getInstance() =
            instance ?: synchronized(this){
                instance ?: Encryptor().also { instance = it }
            }
    }

    fun encrypt(str: String) : String {

        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE,secret,iv)
        val en = cipher.doFinal(str.encodeToByteArray())
        return Gson().toJson(en,ByteArray::class.java)
    }

    fun decode(str: String) : String {
        val jpass = Gson().fromJson(str,ByteArray::class.java)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.DECRYPT_MODE,secret,iv)
        return cipher.doFinal(jpass).decodeToString()
    }
}