package com.example.jaywarehouse.data.manual_putaway

import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.ResultMessageModel
import com.example.jaywarehouse.data.common.utils.getResult
import com.example.jaywarehouse.data.manual_putaway.repository.ManualPutawayDetailModel
import com.example.jaywarehouse.data.manual_putaway.repository.ManualPutawayModel
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class ManualPutawayRepository(
    private val api: ManualPutawayApi
) {
    fun getManualPutawayList(
        keyword: String,
        page: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<ManualPutawayModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        return getResult(
            request = {
                api.getManualPutawayList(jsonObject,page,10,sort,order)
            }
        )
    }


    fun scanManualPutaway(
        locationCode: String,
        warehouseId: String,
        receiptDetailId: String,
        receiptId: String,
        productInventoryId: String,
        productId: String,
        quantity: Int
    ) : Flow<BaseResult<ResultMessageModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("WarehouseLocationCode",locationCode)
        jsonObject.addProperty("WarehouseID",warehouseId)
        jsonObject.addProperty("ReceiptDetailID",receiptDetailId)
        jsonObject.addProperty("ReceiptID",receiptId)
        jsonObject.addProperty("ProductInventoryID",productInventoryId)
        jsonObject.addProperty("ProductID",productId)
        jsonObject.addProperty("Quantity",quantity)
        return getResult(
            request = {
                api.putawayManualScan(jsonObject)
            }
        )
    }

    fun removeManualPutaway(
        productLocationActivityId: String
    ) : Flow<BaseResult<ResultMessageModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ProductLocationActivityID",productLocationActivityId)
        return getResult(
            request = {
                api.putawayManualRemove(jsonObject)
            }
        )
    }

    fun finishManualPutaway(
        receiptDetailId: String,
    ) : Flow<BaseResult<ResultMessageModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ReceiptDetailID",receiptDetailId)
        return getResult(
            request = {
                api.putawayManualFinish(jsonObject)
            }
        )
    }

    fun getManualPutawayDetail(
        keyword: String,
        receiptDetailId: String,
        page: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<ManualPutawayDetailModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        jsonObject.addProperty("ReceiptDetailID",receiptDetailId)
        return getResult(
            request = {
                api.getManualPutawayDetail(jsonObject,page,10,sort,order)
            }
        )
    }
}