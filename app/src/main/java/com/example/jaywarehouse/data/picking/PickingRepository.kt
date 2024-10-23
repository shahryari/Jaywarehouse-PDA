package com.example.jaywarehouse.data.picking

import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.getResult
import com.example.jaywarehouse.data.picking.models.CustomerToPickModel
import com.example.jaywarehouse.data.picking.models.PickedRemoveModel
import com.example.jaywarehouse.data.picking.models.PickedScanItemsModel
import com.example.jaywarehouse.data.picking.models.ReadyToPickModel
import com.example.jaywarehouse.data.putaway.model.ScanModel
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class PickingRepository(private val api: PickingApi) {

    suspend fun getCustomerToPick(
        keyword: String,
        page: Int,
        rows: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<CustomerToPickModel>>{
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        return getResult(
            request = {
                api.getCustomerToPick(jsonObject, page, rows, sort, order)
            }
        )
    }

    suspend fun getReadyToPicked(
        keyword: String,
        customerID: Int,
        page: Int,
        rows: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<ReadyToPickModel>>{
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        jsonObject.addProperty("CustomerID",customerID)
        return getResult(
            request = {
                api.getReadyToPicked(jsonObject, page, rows, sort, order)
            }
        )
    }

    suspend fun scanPicking(
        barcode: String,
        quantity: Int,
        locationCode: String,
        customerId: Int
    ) : Flow<BaseResult<ScanModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("LocationCode",locationCode.trim().trimIndent())
        jsonObject.addProperty("Barcode",barcode.trim().trimIndent())
        jsonObject.addProperty("Quantity",quantity)
        jsonObject.addProperty("CustomerID",customerId)
        return getResult(
            request = {
                api.scanPicking(jsonObject)
            }
        )
    }

    suspend fun removePickedScan(
        pickingScanID: Int
    ) : Flow<BaseResult<PickedRemoveModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("PickingScanID",pickingScanID)
        return getResult(
            request = {
                api.removePicked(jsonObject)
            }
        )
    }

    suspend fun getPickedScanItems(
        keyword: String,
        customerId: Int,
        barcode: String,
        locationCode: String,
        page: Int,
        rows: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<PickedScanItemsModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        jsonObject.addProperty("CustomerID",customerId)
        jsonObject.addProperty("Barcode",barcode)
        jsonObject.addProperty("LocationCode",locationCode)
        return getResult(
            request = {
                api.getPickedScanItems(jsonObject, page, rows, sort, order)
            }
        )
    }

}