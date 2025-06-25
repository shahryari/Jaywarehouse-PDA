package com.linari.data.manual_putaway

import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.common.utils.ResultMessageModel
import com.linari.data.common.utils.getResult
import com.linari.data.manual_putaway.models.ManualPutawayDetailModel
import com.linari.data.manual_putaway.models.ManualPutawayModel
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class ManualPutawayRepository(
    private val api: ManualPutawayApi
) {
    fun getManualPutawayList(
        keyword: String,
        receiptID: Int,
        page: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<ManualPutawayModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        jsonObject.addProperty("ReceiptID",receiptID)
        return getResult(
            request = {
                api.getManualPutawayList(jsonObject,page,ROW_COUNT,sort,order)
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
                api.getManualPutawayDetail(jsonObject,page,100,sort,order)
            }
        )
    }

    fun putawayManualDone(
        putawayDetailID: Int
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("PutawayDetailID",putawayDetailID)
            api.putawayManualDone(jsonObject)
        }
    )
}