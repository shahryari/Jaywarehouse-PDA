package com.example.jaywarehouse.data.putaway

import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.getResult
import com.example.jaywarehouse.data.putaway.model.ScanModel
import com.example.jaywarehouse.data.putaway.model.PutRemoveModel
import com.example.jaywarehouse.data.putaway.model.PutawaysModel
import com.example.jaywarehouse.data.putaway.model.ReadyToPutModel
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class PutawayRepository(
    private val api: PutawayApi
) {
    suspend fun getReadyToPut(
        keyword: String,
        page: Int,
        rows: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<ReadyToPutModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        return getResult(
            request = {
                api.getReadyToPut(jsonObject, page, rows, sort, order)
            }
        )
    }

    suspend fun put(
        receivingDetailID: Int,
        locationCode: String,
        barcode: String,
        boxNumber: String,
        quantity: Int
    ) : Flow<BaseResult<ScanModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ReceivingDetailID",receivingDetailID)
        jsonObject.addProperty("LocationCode",locationCode.trim().trimIndent())
        jsonObject.addProperty("Barcode",barcode.trim().trimIndent())
        jsonObject.addProperty("BoxNumber",boxNumber.trim().trimIndent())
        jsonObject.addProperty("Quantity",quantity)
        return getResult(
            request = {
                api.put(jsonObject)
            }
        )
    }


    suspend fun getPutaways(
        receivingDetailID: Int,
        keyword: String,
        page: Int,
        rows: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<PutawaysModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ReceivingDetailID",receivingDetailID)
        jsonObject.addProperty("Keyword",keyword)
        return getResult(
            request = {
                api.getPutaways(jsonObject,page, rows, sort, order)
            }
        )
    }


    suspend fun putRemove(
        putawayScanID: Int
    ) : Flow<BaseResult<PutRemoveModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("PutawayScanID",putawayScanID)
        return getResult(
            request = {
                api.putRemove(jsonObject)
            }
        )
    }
}