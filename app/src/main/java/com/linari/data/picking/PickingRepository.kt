package com.linari.data.picking

import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.common.utils.ResultMessageModel
import com.linari.data.common.utils.getResult
import com.linari.data.picking.models.PickingListGroupedModel
import com.linari.data.picking.models.PickingListModel
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class PickingRepository(private val api: PickingApi) {

    fun getPickingListGrouped(
        keyword: String,
        warehouseID: Int,
        page: Int,
        rows: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<PickingListGroupedModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        jsonObject.addProperty("WarehouseID",warehouseID)
        return getResult(
            request = {
                api.getPickingListGrouped(jsonObject, page, rows, sort, order)
            }
        )
    }

    fun getPickingList(
        keyword: String,
        customerId: String,
        warehouseID: Int,
        page: Int,
        rows: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<PickingListModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        jsonObject.addProperty("CustomerID",customerId)
        jsonObject.addProperty("WarehouseID",warehouseID)
        return getResult(
            request = {
                api.getPickingList(jsonObject, page, rows, sort, order)
            }
        )
    }

    fun completePicking(
        locationCode: String,
        barcode: String,
        pickingID: String,
        warehouseID: Int,
        shippingOrderID: Int
    ) : Flow<BaseResult<ResultMessageModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("LocationCode",locationCode)
        jsonObject.addProperty("ProductBarcodeNumber",barcode)
        jsonObject.addProperty("PickingID",pickingID)
        jsonObject.addProperty("WarehouseID",warehouseID)
        jsonObject.addProperty("ShippingOrderID",shippingOrderID)
        return getResult(
            request = {
                api.completePicking(jsonObject)
            }
        )
    }


    fun getPurchaseOrderListBD(
        keyword: String,
        warehouseID: Int,
        page: Int,
        sort: String,
        order: String
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("Keyword",keyword)
            jsonObject.addProperty("WarehouseID",warehouseID)
            api.getPurchaseOrderListBD(jsonObject,page,ROW_COUNT,sort,order)
        }
    )

    fun getPurchaseOrderDetailListBD(
        keyword: String,
        purchaseOrderID: String,
        page: Int,
        sort: String,
        order: String
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("Keyword",keyword)
            jsonObject.addProperty("PurchaseOrderID",purchaseOrderID)
            api.getPurchaseOrderDetailListBD(jsonObject,page,ROW_COUNT,sort,order)
        }
    )

    fun getShippingOrderDetailListBD(
        keyword: String,
        purchaseOrderDetailID: Int,
        page: Int,
        sort: String,
        order: String
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("Keyword",keyword)
            jsonObject.addProperty("PurchaseOrderDetailID",purchaseOrderDetailID)
            api.getPickingListBD(jsonObject,page,ROW_COUNT,sort,order)
        }
    )

    fun finishPurchaseOrderDetailBD(
        purchaseOrderDetailID: Int,
        warehouseID: Int
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("PurchaseOrderDetailID",purchaseOrderDetailID)
            jsonObject.addProperty("WarehouseID",warehouseID)
            api.finishPurchaseOrderDetailBD(jsonObject)
        }
    )


    fun modifyPickQuantityBD(
        pickingID: Int,
        purchaseOrderDetailID: Int,
        quantity: Double
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("PickingID",pickingID)
            jsonObject.addProperty("PurchaseOrderDetailID",purchaseOrderDetailID)
            jsonObject.addProperty("NewQty",quantity)
            api.modifyPickQuantityBD(jsonObject)
        }
    )


    fun wasteOnPicking(
        pickingID: Int,
        purchaseOrderDetailID: Int,
        quantity: Double
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("PickingID",pickingID)
            jsonObject.addProperty("PurchaseOrderDetailID",purchaseOrderDetailID)
            jsonObject.addProperty("Quantity",quantity)
            api.wasteOnPicking(jsonObject)
        }
    )



}