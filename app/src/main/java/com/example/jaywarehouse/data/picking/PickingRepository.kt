package com.example.jaywarehouse.data.picking

import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.ResultMessageModel
import com.example.jaywarehouse.data.common.utils.getResult
import com.example.jaywarehouse.data.picking.models.PickingListGroupedModel
import com.example.jaywarehouse.data.picking.models.PickingListModel
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class PickingRepository(private val api: PickingApi) {

    fun getPickingListGrouped(
        keyword: String,
        page: Int,
        rows: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<PickingListGroupedModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        return getResult(
            request = {
                api.getPickingListGrouped(jsonObject, page, rows, sort, order)
            }
        )
    }

    fun getPickingList(
        keyword: String,
        customerId: String,
        page: Int,
        rows: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<PickingListModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        jsonObject.addProperty("CustomerID",customerId)
        return getResult(
            request = {
                api.getPickingList(jsonObject, page, rows, sort, order)
            }
        )
    }

    fun completePicking(
        locationCode: String,
        barcode: String,
        productLocationActivityId: String
    ) : Flow<BaseResult<ResultMessageModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("LocationCode",locationCode)
        jsonObject.addProperty("ProductBarcodeNumber",barcode)
        jsonObject.addProperty("ProductLocationActivityID",productLocationActivityId)
        return getResult(
            request = {
                api.completePicking(JsonObject())
            }
        )
    }

}