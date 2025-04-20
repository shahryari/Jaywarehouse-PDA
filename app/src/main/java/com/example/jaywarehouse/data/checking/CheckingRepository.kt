package com.example.jaywarehouse.data.checking

import com.example.jaywarehouse.data.checking.models.CheckingListGroupedModel
import com.example.jaywarehouse.data.checking.models.CheckingListModel
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.ResultMessageModel
import com.example.jaywarehouse.data.common.utils.getResult
import com.example.jaywarehouse.presentation.common.composables.BaseListItemModel
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class CheckingRepository(
    private val api: CheckingApi
) {

    fun getCheckingListGroupedModel(
        keyword: String,
        page: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<CheckingListGroupedModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        return getResult(
            request = {
                api.getCheckingListGrouped(jsonObject, page, 10, sort, order)
            }
        )
    }

    fun getCheckingList(
        keyword: String,
        customerId: String,
        page: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<CheckingListModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        jsonObject.addProperty("CustomerID",customerId)
        return getResult(
            request = {
                api.getCheckingList(jsonObject, page, 10, sort, order)
            }
        )
    }

    fun checking(
        isCrossDock: Boolean,
        checkCount: Double,
        customerId: String,
        checkingId: String,
        palletBarcode: String
    ) : Flow<BaseResult<ResultMessageModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("IsCrossDock",isCrossDock)
        jsonObject.addProperty("CheckCount",checkCount)
        jsonObject.addProperty("CustomerID",customerId)
        jsonObject.addProperty("CheckingID",checkingId)
        jsonObject.addProperty("PalletBarcode",palletBarcode)
        return getResult(
            request = {
                api.checking(jsonObject)
            }
        )
    }
}