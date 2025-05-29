package com.example.jaywarehouse.data.putaway

import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.ROW_COUNT
import com.example.jaywarehouse.data.common.utils.ResultMessageModel
import com.example.jaywarehouse.data.common.utils.getResult
import com.example.jaywarehouse.data.putaway.model.ScanModel
import com.example.jaywarehouse.data.putaway.model.PutawayListModel
import com.example.jaywarehouse.data.putaway.model.PutawayListGroupedModel
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class PutawayRepository(
    private val api: PutawayApi
) {
    fun getPutawayListGrouped(
        keyword: String,
        page: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<PutawayListGroupedModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
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