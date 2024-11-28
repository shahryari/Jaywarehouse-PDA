package com.example.jaywarehouse.data.rs

import com.example.jaywarehouse.data.common.utils.getResult
import com.google.gson.JsonObject

class RSRepository(
    private val api: RSApi
) {


    fun getPODInvoice(
        keyword: String,
        page: Int,
        sort: String,
        order: String
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("Keyword",keyword)
            api.getPodInvoice(jsonObject,page,10,sort,order)
        }
    )

    fun updateDriver(
        shippingId: String,
        driverFullName: String,
        driverTin: String,
        carNumber: String,
        trailerNumber: String
    ) = getResult(
        request = {
            val jsonObject = JsonObject().apply {
                addProperty("ShippingID",shippingId)
                addProperty("DriverFullName",driverFullName)
                addProperty("DriverTin",driverTin)
                addProperty("CarNumber",carNumber)
                addProperty("TrailerNumber",trailerNumber)
            }
            api.updateDriver(jsonObject)
        }
    )

    fun getDriverInfo(
        driverTin: String
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("DriverTin",driverTin)
            api.driverInfo(jsonObject)
        }
    )

    fun rsInterface(
        pODInvoiceID: String
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("PODInvoiceID",pODInvoiceID)
            api.rsInterface(jsonObject)
        }
    )
}