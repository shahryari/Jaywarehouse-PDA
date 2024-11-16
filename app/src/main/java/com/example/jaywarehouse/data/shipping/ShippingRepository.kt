package com.example.jaywarehouse.data.shipping

import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.ResultMessageModel
import com.example.jaywarehouse.data.common.utils.getResult
import com.example.jaywarehouse.data.pallet.model.PalletConfirmRow
import com.example.jaywarehouse.data.shipping.models.DriverModel
import com.example.jaywarehouse.data.shipping.models.PalletInShippingModel
import com.example.jaywarehouse.data.shipping.models.PalletInShippingRow
import com.example.jaywarehouse.data.shipping.models.ShippingModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class ShippingRepository(private val api: ShippingApi) {


    fun getShipping(
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

    fun getPalletListInShipping(
        shippingId: Int
    ) : Flow<BaseResult<PalletInShippingModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ShippingID",shippingId)
        return getResult(
            request = {
                api.getPalletListInShipping(jsonObject, 1, 100, "CreatedOn", "desc")
            }
        )
    }

    fun confirmShipping(
        shippingId: Int
    ) : Flow<BaseResult<ResultMessageModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ShippingID",shippingId)
        return getResult(
            request = {
                api.confirmShipping(jsonObject)
            }
        )
    }

    fun createInvoice(
        shippingId: Int
    ) : Flow<BaseResult<ResultMessageModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ShippingID",shippingId)
        return getResult(
            request = {
                api.createInvoice(jsonObject)
            }
        )
    }

    fun createRSInterface(
        shippingId: Int,
        shippingNumber: String
    ) : Flow<BaseResult<ResultMessageModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ShippingID",shippingId)
        jsonObject.addProperty("ShippingNumber",shippingNumber)
        return getResult(
            request = {
                api.createRSInterface(jsonObject)
            }
        )
    }

    fun palletBarcodeCheck(
        barcode: String
    ) : Flow<BaseResult<PalletConfirmRow>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("PalletBarcode",barcode)
        return getResult(
            request = {
                api.palletBarcodeCheck(jsonObject)
            }
        )
    }

    fun getDriverInfo(
        driverTin: String
    ) : Flow<BaseResult<DriverModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("DriverTin",driverTin)
        return getResult(
            request = {
                api.getDriverInfo(jsonObject)
            }
        )
    }

    fun createShipping(
        pallets: List<PalletConfirmRow>,
        driverFullName: String,
        driverTin: String,
        carNumber: String,
        trailerNumber: String
    ) : Flow<BaseResult<ResultMessageModel>>{
        val jsonObject = JsonObject()

        val palletArray = JsonArray()
        pallets.map {
            val palletObject = JsonObject()
            palletObject.addProperty("PalletManifestID",it.palletManifestID)
            palletObject.addProperty("PalletBarcode",it.palletBarcode)
            palletObject
        }.forEach {
            palletArray.add(it)
        }

        jsonObject.addProperty("DriverFullName",driverFullName)
        jsonObject.addProperty("DriverTin",driverTin)
        jsonObject.addProperty("CarNumber",carNumber)
        jsonObject.addProperty("TrailerNumber",trailerNumber)
        jsonObject.add("PalletInShippingSubmitList",palletArray)
        return getResult(
            request = {
                api.submitShipping(jsonObject)
            }
        )
    }

    fun getShippingCustomers(shippingId: Int) = getResult(
        request = {
            api.getShippingCustomers(
                jsonObject = JsonObject().apply {
                    addProperty("ShippingID", shippingId)
                },
                1,
                100,
                "CreatedOn",
                "desc"
            )
        }
    )

    fun getShippingPalletTypes() = getResult(
        request = {
            api.getShippingPalletType(
//                jsonObject = JsonObject().apply {
//                    addProperty("ShippingID", shippingId)
//                },
//                1,
//                100,
//                "CreatedOn",
//                "desc"
            )
        }
    )

    fun submitPalletShipping(
        pallets: List<PalletInShippingRow>,
        warehouseId: Int,
    ) : Flow<BaseResult<ResultMessageModel>> {

        val jsonArray = JsonArray()

        pallets.map {
            val palletObject = JsonObject()
            palletObject.addProperty("ShippingID",it.shippingID)
            palletObject.addProperty("CustomerID",it.customerID)
            palletObject.addProperty("PalletTypeID",it.palletTypeID)
            palletObject.addProperty("PalletQuantity",it.palletQuantity)
            palletObject.addProperty("WarehouseID",warehouseId)
            palletObject.addProperty("EntityState",it.entityState)
            palletObject
        }.forEach {
            jsonArray.add(it)
        }
        return getResult(
            request = {
                api.submitShippingPallet(jsonArray)
            }
        )
    }
}