package com.example.jaywarehouse.data.receiving.repository

import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.getResult
import com.example.jaywarehouse.data.receiving.ReceivingApi
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailModel
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailScanModel
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailScanRemoveModel
import com.example.jaywarehouse.data.receiving.model.ReceivingModel
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class ReceivingRepository(
    private val api: ReceivingApi
) {



    suspend fun getReceivingList(
        keyword: String,
        page: Int,
        rows: Int,
        order: String,
        sort: String
    ) : Flow<BaseResult<ReceivingModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        return getResult(
            request = {
                api.getReceivingList(jsonObject,page, rows, sort, order)
            }
        )
    }

    suspend fun getReceivingDetails(
        receivingID: Int,
        keyword: String,
        page: Int,
        rows: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<ReceivingDetailModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ReceivingID",receivingID)
        jsonObject.addProperty("Keyword",keyword)
        return getResult(
            request = {
                api.getReceivingDetails(jsonObject, page, rows, sort, order)
            }
        )
    }

    suspend fun scanReceivingDetail(
        receivingID: Int,
        barcode: String,
        quantity: Int
    ) : Flow<BaseResult<ReceivingDetailScanModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ReceivingID",receivingID)
        jsonObject.addProperty("Barcode",barcode.trim().trimIndent())
        jsonObject.addProperty("Quantity",quantity)
        return getResult(
            request = {
                api.scanReceivingDetail(jsonObject)
            }
        )
    }

    suspend fun removeReceivingDetailScan(
        receivingID: Int,
        barcode: String
    ) : Flow<BaseResult<ReceivingDetailScanRemoveModel>>{
        val jsonObject = JsonObject()
        jsonObject.addProperty("ReceivingID",receivingID)
        jsonObject.addProperty("Barcode",barcode)
        return getResult(
            request = {
                api.removeReceivingDetailScan(jsonObject)
            }
        )
    }
}