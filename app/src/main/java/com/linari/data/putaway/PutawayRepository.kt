package com.linari.data.putaway

import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.common.utils.ResultMessageModel
import com.linari.data.common.utils.getResult
import com.linari.data.putaway.model.ScanModel
import com.linari.data.putaway.model.PutawayListModel
import com.linari.data.putaway.model.PutawayListGroupedModel
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class PutawayRepository(
    private val api: PutawayApi
) {
    fun getPutawayListGrouped(
        keyword: String,
        warehouseID: Int,
        page: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<PutawayListGroupedModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        jsonObject.addProperty("WarehouseID",warehouseID)
        return getResult(
            request = {
                api.getPutawayListGrouped(
                    jsonObject = jsonObject,
                    page = page,
                    rows = ROW_COUNT,
                    sort = sort,
                    order = order
                )
            }
        )
    }

    fun getPutawayList(
        keyword: String,
        receiptId: Int,
        page: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<PutawayListModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        jsonObject.addProperty("ReceiptID",receiptId)

        return getResult(
            request = {
                api.getPutawayList(
                    jsonObject = jsonObject,
                    page = page,
                    rows = ROW_COUNT,
                    sort = sort,
                    order =order
                )
            }
        )
    }

    fun finishPutaway(
        receiptDetailId: String,
        productLocationActivityId: String,
        receivingDetailId: String
    ) : Flow<BaseResult<ResultMessageModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ReceiptDetailID",receiptDetailId)
        jsonObject.addProperty("ProductLocationActivityID",productLocationActivityId)
        jsonObject.addProperty("ReceivingDetailID",receivingDetailId)
        return getResult(
            request = {
                api.finishPutaway(
                    jsonObject
                )
            }
        )
    }
}