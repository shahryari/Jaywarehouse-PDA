package com.linari.data.return_receiving

import com.google.gson.JsonObject
import com.linari.data.common.utils.getResult

class ReturnRepository(
    private val api: ReturnApi
) {

    fun getReturnReceivingList(
        keyword: String,
        warehouseID: Long,
        sort: String,
        order: String,
        page: Int,
        rows: Int
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("Keyword",keyword)
            jsonObject.addProperty("WarehouseID",warehouseID)
            api.getReturnReceivingList(jsonObject,page,rows,sort,order)
        }
    )

    fun getReceivingDetails(
        keyword: String,
        receivingID: Long,
        sort: String,
        order: String,
        page: Int,
        rows: Int
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("Keyword",keyword)
            jsonObject.addProperty("ReceivingID",receivingID)
            api.getReceivingDetails(jsonObject,page,rows,sort,order)
        }
    )

    fun removeReturnReceiving(
        receivingID: Long
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("ReceivingID",receivingID)
            api.removeReturnReceiving(jsonObject)
        }
    )

    fun removeReturnReceivingDetail(
        receivingDetailID: Long
    ) = getResult(
        request =  {
            val jsonObject = JsonObject()
            jsonObject.addProperty("ReceivingDetailID",receivingDetailID)
            api.removeReturnReceivingDetail(jsonObject)
        }
    )

    fun saveReturnReceiving(
        receivingReferenceNumber: String,
        receivingDate: String,
        warehouseID: Long,
        partnerID: Long,
        ownerInfoID: Long
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("ReceivingReferenceNumber",receivingReferenceNumber)
            jsonObject.addProperty("ReceivingDate",receivingDate)
            jsonObject.addProperty("WarehouseID",warehouseID)
            jsonObject.addProperty("PartnerID",partnerID)
            jsonObject.addProperty("OwnerInfoID",ownerInfoID)
            api.saveReturnReceiving(jsonObject)
        }
    )

    fun saveReturnReceivingDetail(
        receivingID: Long,
        warehouseID: Long,
        quantity: Double,
        quiddityTypeID: Long,
        barcode: String
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("ReceivingID",receivingID)
            jsonObject.addProperty("WarehouseID",warehouseID)
            jsonObject.addProperty("quantity",quantity)
            jsonObject.addProperty("QuiddityTypeID",quiddityTypeID)
            jsonObject.addProperty("Barcode",barcode)
            api.saveReturnReceivingDetail(jsonObject)
        }
    )

    fun getCustomerList() = getResult(
        request = {
            api.getCustomerList()
        }
    )

    fun getOwnerInfoList() = getResult(
        request = {
            api.getOwnerInfoList()
        }
    )

    fun getProductStatuses() = getResult(
        request = {
            api.getProductStatus(1,100,"","")
        }
    )
}