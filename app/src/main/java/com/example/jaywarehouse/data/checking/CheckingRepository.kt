package com.example.jaywarehouse.data.checking

import com.example.jaywarehouse.data.checking.models.CheckingListGroupedModel
import com.example.jaywarehouse.data.checking.models.CheckingListModel
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.ROW_COUNT
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
                api.getCheckingListGrouped(jsonObject, page, ROW_COUNT, sort, order)
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
                api.getCheckingList(jsonObject, page, ROW_COUNT, sort, order)
            }
        )
    }

    fun checking(
        isCrossDock: Boolean,
        checkCount: Double,
        customerId: String,
        checkingId: String,
        palletBarcode: String,
        palletStatusID: Int,
        palletTypeID: Int
    ) : Flow<BaseResult<ResultMessageModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("IsCrossDock",isCrossDock)
        jsonObject.addProperty("CheckCount",checkCount)
        jsonObject.addProperty("CustomerID",customerId)
        jsonObject.addProperty("CheckingID",checkingId)
        jsonObject.addProperty("PalletBarcode",palletBarcode)
        jsonObject.addProperty("PalletStatusID",palletStatusID)
        jsonObject.addProperty("PalletTypeID",palletTypeID)
        return getResult(
            request = {
                api.checking(jsonObject)
            }
        )
    }

    fun getPalletTypes() = getResult(
        request = {
            api.getPalletTypes()
        }
    )

    fun getPalletStatuses() = getResult(
        request = {
            api.getPalletStatuses()
        }
    )

    fun getPalletMask(
        warehouseID: String
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("WarehouseID",warehouseID)
            api.getPalletMask(jsonObject)
        }
    )
}