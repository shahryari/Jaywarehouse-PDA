package com.example.jaywarehouse.data.transfer

import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.getResult
import com.example.jaywarehouse.data.transfer.models.LocationTransferModel
import com.example.jaywarehouse.data.transfer.models.TransferModel
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class TransferRepository(private val api: TransferApi) {
    suspend fun getTransfers(
        keyword: String,
        page: Int,
        rows: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<LocationTransferModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        return getResult(
            request = {
                api.getLocationInventory(jsonObject, page, rows, sort, order)
            }
        )
    }

    suspend fun pickTransfer(
        locationCode: String,
        barcode: String,
        boxNumber: String
    ) : Flow<BaseResult<TransferModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("LocationCode",locationCode.trim().trimIndent())
        if (barcode.isNotEmpty())jsonObject.addProperty("Barcode",barcode.trim().trimIndent())
        if (boxNumber.isNotEmpty())jsonObject.addProperty("BoxNumber",boxNumber.trim().trimIndent())
        return getResult(
            request = {
                api.pickTransfer(jsonObject)
            }
        )
    }

    suspend fun putTransfer(
        locationCode: String,
        barcode: String,
        boxNumber: String
    ) : Flow<BaseResult<TransferModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("LocationCode",locationCode.trim().trimIndent())
        if (barcode.isNotEmpty())jsonObject.addProperty("Barcode",barcode.trim().trimIndent())
        if (boxNumber.isNotEmpty())jsonObject.addProperty("BoxNumber",boxNumber.trim().trimIndent())
        return getResult(
            request = {
                api.putTransfer(jsonObject)
            }
        )
    }
}