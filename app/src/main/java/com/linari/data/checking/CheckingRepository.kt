package com.linari.data.checking

import com.linari.data.checking.models.CheckingListGroupedModel
import com.linari.data.checking.models.CheckingListModel
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.common.utils.ResultMessageModel
import com.linari.data.common.utils.getResult
import com.linari.presentation.common.composables.BaseListItemModel
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class CheckingRepository(
    private val api: CheckingApi
) {

    fun getCheckingListGroupedModel(
        keyword: String,
        warehouseID: Int,
        page: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<CheckingListGroupedModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        jsonObject.addProperty("WarehouseID",warehouseID)
        return getResult(
            request = {
                api.getCheckingListGrouped(jsonObject, page, ROW_COUNT, sort, order)
            }
        )
    }

    fun getCheckingList(
        keyword: String,
        customerId: String,
        warehouseID: Int,
        page: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<CheckingListModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        jsonObject.addProperty("CustomerID",customerId)
        jsonObject.addProperty("WarehouseID",warehouseID)
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
            val jsonObject = JsonObject()
            jsonObject.addProperty("IsBox",false)
            api.getPalletTypes(jsonObject)
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

    fun getPalletManifestInfo(
        palletBarcode: String
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("PalletBarcode",palletBarcode)
            api.getPalletManifestInfo(jsonObject)
        }
    )

    fun cancelPicking(
        checkingID: Int,
        quantity: Double,
        warehouseLocationCode: String,
        isDamaged: Boolean
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("CheckingID",checkingID)
            jsonObject.addProperty("Quantity",quantity)
            jsonObject.addProperty("WarehouseLocationCode",warehouseLocationCode)
            jsonObject.addProperty("IsDamaged",isDamaged)
            api.cancelPick(jsonObject)
        }
    )
}