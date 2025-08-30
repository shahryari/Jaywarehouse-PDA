package com.linari.data.transfer

import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.common.utils.ResultMessageModel
import com.linari.data.common.utils.getResult
import com.linari.data.transfer.models.TransferModel
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class TransferRepository(private val api: TransferApi) {
    fun getTransfers(
        productCode: String,
        barcode: String,
        locationCode: String,
        warehouseID: Long,
        page: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<TransferModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("LocationCode",locationCode)
        jsonObject.addProperty("ProductCode",productCode)
        jsonObject.addProperty("ProductBarcodeNumber",barcode)
        jsonObject.addProperty("WarehouseID",warehouseID)
        return getResult(
            request = {
                api.getTransferList(jsonObject, page, ROW_COUNT, sort, order)
            }
        )
    }

    fun transferLocation(
        quiddityTypeToId: Long,
        warehouseLocationToId: Long,
        warehouseLocationCodeTo: String,
        locationInventoryId: Long,
        expireDateTo: String,
        quantity: Double,
        warehouseId: Long
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
        warehouseID: Long
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("Keyword",keyword)
            jsonObject.addProperty("WarehouseID",warehouseID)
            api.getWarehouseLocations(jsonObject,1,100,"CreatedOn","desc")
        }
    )
}