package com.example.jaywarehouse.data.transfer

import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.ROW_COUNT
import com.example.jaywarehouse.data.common.utils.ResultMessageModel
import com.example.jaywarehouse.data.common.utils.getResult
import com.example.jaywarehouse.data.transfer.models.TransferModel
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class TransferRepository(private val api: TransferApi) {
    fun getTransfers(
        keyword: String,
        page: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<TransferModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        return getResult(
            request = {
                api.getTransferList(jsonObject, page, ROW_COUNT, sort, order)
            }
        )
    }

    fun transferLocation(
        quiddityTypeToId: Int,
        warehouseLocationToId: Int,
        warehouseLocationCodeTo: String,
        locationInventoryId: Int,
        expireDateTo: String,
        quantity: Double,
        warehouseId: Int
    ) : Flow<BaseResult<ResultMessageModel>> {
        val jsonObject = JsonObject()

        jsonObject.addProperty("QuiddityTypeToID",quiddityTypeToId)
        jsonObject.addProperty("WarehouseLocationToID",warehouseLocationToId)
        jsonObject.addProperty("WarehouseLocationCodeTo",warehouseLocationCodeTo)
        jsonObject.addProperty("LocationInventoryID",locationInventoryId)
        jsonObject.addProperty("ExpireDateTo",expireDateTo)
        jsonObject.addProperty("Quantity",quantity)
        jsonObject.addProperty("WarehouseID",warehouseId)
        return getResult(
            request = {
                api.transferLocation(jsonObject)
            }
        )
    }

    fun getProductStatuses() = getResult(
        request = {
            api.getProductStatus(1,100,"CreatedOn","desc")
        }
    )

    fun getWarehouseLocations(
        keyword: String,
        warehouseID: Int
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("Keyword",keyword)
            jsonObject.addProperty("WarehouseID",warehouseID)
            api.getWarehouseLocations(jsonObject,1,100,"CreatedOn","desc")
        }
    )
}