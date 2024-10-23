package com.example.jaywarehouse.data.shipping

import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.getResult
import com.example.jaywarehouse.data.shipping.models.DriverModel
import com.example.jaywarehouse.data.shipping.models.ShipModel
import com.example.jaywarehouse.data.shipping.models.ShippingDetailModel
import com.example.jaywarehouse.data.shipping.models.ShippingModel
import com.example.jaywarehouse.presentation.common.utils.Order
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class ShippingRepository(private val api: ShippingApi) {
    suspend fun getDrivers(
//        keyword: String,
//        page: Int,
//        row: Int,
//        sort: String,
//        order: String
    ) : Flow<BaseResult<DriverModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword","")
        return getResult(
            request = {
                api.getDrivers(jsonObject, 1, 100, "CreatedOn", Order.Asc.value)
            }
        )
    }

    suspend fun getShipping(
        keyword: String,
        page: Int,
        row: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<ShippingModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        return getResult(
            request = {
                api.getShipping(jsonObject, page, row, sort, order)
            }
        )
    }

    suspend fun getShippingDetail(
        shippingId: Int,
        keyword: String,
        page: Int,
        row: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<ShippingDetailModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        jsonObject.addProperty("ShippingID",shippingId)
        return getResult(
            request = {
                api.getShippingDetail(jsonObject, page, row, sort, order)
            }
        )
    }

    suspend fun ship(
//        shippingNumber: Int,
        driverId: Int
    ) : Flow<BaseResult<ShipModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("DriverID",driverId)
        return getResult(
            request = {
                api.ship(jsonObject)
            }
        )
    }

    suspend fun invoice(
        shippingId: Int
    ) : Flow<BaseResult<ShipModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ShippingID",shippingId)
        return getResult(
            request = {
                api.invoice(jsonObject)
            }
        )
    }

    suspend fun removeShip(
        shippingId: Int
    ) : Flow<BaseResult<ShipModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ShippingID",shippingId)
        return getResult(
            request = {
                api.removeShip(jsonObject)
            }
        )
    }

    suspend fun addShippingDetail(
        shippingId: Int,
        packingNumber: String,
    ) : Flow<BaseResult<ShipModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ShippingID",shippingId)
        jsonObject.addProperty("PackingNumber",packingNumber.trim().trimIndent())
        return getResult(
            request = {
                api.addShippingDetail(jsonObject)
            }
        )
    }
    suspend fun removeShippingDetail(
        packingId: Int
    ) : Flow<BaseResult<ShipModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("PackingID",packingId)
        return getResult(
            request = {
                api.removeShippingDetail(jsonObject)
            }
        )
    }
}