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
        putawayId: Int,
        quantity: Double
    ) : Flow<BaseResult<ResultMessageModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("WarehouseLocationCode",locationCode)
        jsonObject.addProperty("WarehouseID",warehouseId)
        jsonObject.addProperty("PutawayID",putawayId)
        jsonObject.addProperty("Quantity",quantity)
        return getResult(
            request = {
                api.putawayManualScan(jsonObject)
            }
        )
    }

    fun removeManualPutaway(
        putawayDetailID: String
    ) : Flow<BaseResult<ResultMessageModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("PutawayDetailID",putawayDetailID)
        return getResult(
            request = {
                api.putawayManualRemove(jsonObject)
            }
        )
    }

    fun finishManualPutaway(
        receiptDetailId: String,
        putawayID: String
    ) : Flow<BaseResult<ResultMessageModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ReceiptDetailID",receiptDetailId)
        jsonObject.addProperty("PutawayID",putawayID)
        return getResult(
            request = {
                api.putawayManualFinish(jsonObject)
            }
        )
    }

    fun getManualPutawayDetail(
        keyword: String,
        putawayID: Int,
        page: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<ManualPutawayDetailModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        jsonObject.addProperty("PutawayID",putawayID)
        return getResult(
            request = {
                api.getManualPutawayDetail(jsonObject,page,10,sort,order)
            }
        )
    }
}