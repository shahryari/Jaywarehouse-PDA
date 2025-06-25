package com.linari.data.packing

import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.getResult
import com.linari.data.packing.model.PackingCustomerModel
import com.linari.data.packing.model.PackingDetailModel
import com.linari.data.packing.model.PackingModel
import com.linari.data.putaway.model.ScanModel
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class PackingRepository(
    private val api: PackingApi
) {
    suspend fun getPackingCustomers(
        keyword: String,
        page: Int,
        rows: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<PackingCustomerModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        return getResult(
            request = {
                api.getPackingCustomer(jsonObject, page, rows, sort, order)
            }
        )
    }

    suspend fun getPacking(
        keyword: String,
        page: Int,
        rows: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<PackingModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        return getResult(
            request = {
                api.getPacking(jsonObject, page, rows, sort, order)
            }
        )
    }

    suspend fun getPackingDetails(
        packingId: Int,
        keyword: String,
        page: Int,
        rows: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<PackingDetailModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("PackingID",packingId)
        jsonObject.addProperty("Keyword",keyword)
        return getResult(
            request = {
                api.getPackingDetails(jsonObject,page, rows, sort, order)
            }
        )
    }

    suspend fun pack(
        packingNumber: String,
        customerId: Int
    ) : Flow<BaseResult<ScanModel>> = getResult(
        request = {
            api.pack(JsonObject().apply {
                addProperty("PackingNumber", packingNumber.trim().trimIndent())
                addProperty("CustomerID", customerId)
            })
        }
    )

    suspend fun packRemove(
        packingId: Int
    ) : Flow<BaseResult<ScanModel>> = getResult(
        request = {
            api.packRemove(JsonObject().apply {
                addProperty("PackingID", packingId)
            })
        }
    )

    suspend fun addPackingDetail(
        packingId: Int,
        customerId: Int,
        barcode: String
    ) : Flow<BaseResult<ScanModel>> = getResult(
        request = {
            api.addPackingDetail(JsonObject().apply {
                addProperty("PackingID", packingId)
                addProperty("CustomerID", customerId)
                addProperty("Barcode", barcode.trim().trimIndent())
            })
        }
    )

    suspend fun removePackingDetail(
        packingDetailId: Int
    ) : Flow<BaseResult<ScanModel>> {
        return getResult(
            request = {
                api.removePackingDetail(JsonObject().apply {
                    addProperty("PackingDetailID", packingDetailId)
                })
            }
        )
    }

    suspend fun finishPacking(
        packingId: Int,
        isNew: Boolean
    ) : Flow<BaseResult<ScanModel>> = getResult(
        request = {
            api.finishPacking(JsonObject().apply {
                addProperty("PackingID", packingId)
                addProperty("IsNew",isNew)
            })
        }
    )
}