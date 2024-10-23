package com.example.jaywarehouse.data.shipping

import com.example.jaywarehouse.data.common.utils.ORDER
import com.example.jaywarehouse.data.common.utils.PAGE
import com.example.jaywarehouse.data.common.utils.ROWS
import com.example.jaywarehouse.data.common.utils.SORT
import com.example.jaywarehouse.data.shipping.models.DriverModel
import com.example.jaywarehouse.data.shipping.models.ShipModel
import com.example.jaywarehouse.data.shipping.models.ShippingDetailModel
import com.example.jaywarehouse.data.shipping.models.ShippingModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ShippingApi {

    @POST("Drivers")
    suspend fun getDrivers(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<DriverModel>

    @POST("Shippings")
    suspend fun getShipping(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<ShippingModel>

    @POST("ShippingDetails")
    suspend fun getShippingDetail(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<ShippingDetailModel>

    @POST("Ship")
    suspend fun ship(
        @Body jsonObject: JsonObject
    ) : Response<ShipModel>

    @POST("Invoice")
    suspend fun invoice(
        @Body jsonObject: JsonObject
    ) : Response<ShipModel>

    @POST("ShipRemove")
    suspend fun removeShip(
        @Body jsonObject: JsonObject
    ) : Response<ShipModel>

    @POST("ShippingDetailRemove")
    suspend fun removeShippingDetail(
        @Body jsonObject: JsonObject
    ) : Response<ShipModel>

    @POST("ShippingDetailAdd")
    suspend fun addShippingDetail(
        @Body jsonObject: JsonObject
    ) : Response<ShipModel>

}