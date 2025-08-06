package com.linari.data.shipping

import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.ResultMessageModel
import com.linari.data.common.utils.getResult
import com.linari.data.pallet.model.PalletConfirmRow
import com.linari.data.shipping.models.DriverModel
import com.linari.data.shipping.models.PalletInShippingModel
import com.linari.data.shipping.models.PalletInShippingRow
import com.linari.data.shipping.models.ShippingModel
import com.linari.data.shipping.models.ShippingPalletManifestRow
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class ShippingRepository(private val api: ShippingApi) {


    fun getShippings(
        keyword: String,
        warehouseID: Int,
        page: Int,
        row: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<ShippingModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        jsonObject.addProperty("WarehouseID",warehouseID)
        return getResult(
            request = {
                api.getShippings(jsonObject, page, row, sort, order)
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
        pallets: List<ShippingPalletManifestRow>,
        driverFullName: String,
        driverTin: String,
        carNumber: String,
        trailerNumber: String
    ) : Flow<BaseResult<ResultMessageModel>>{
        val jsonObject = JsonObject()

        val palletArray = JsonArray()
        pallets.map {
            val palletObject = JsonObject()
            palletObject.addProperty("PalletManifestID",it.palletManifestId)
            palletObject
        }.forEach {
            palletArray.add(it)
        }

        jsonObject.addProperty("DriverFullName",driverFullName)
        jsonObject.addProperty("DriverTin",driverTin)
        jsonObject.addProperty("CarNumber",carNumber)
        jsonObject.addProperty("TrailerNumber",trailerNumber)
        jsonObject.addProperty("PalletManifestEntities", Gson().toJson(palletArray))
        return getResult(
            request = {
                api.createShipping(jsonObject)
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
                jsonObject = JsonObject().apply {
                }
            )
        }
    )

    fun getShippingPalletStatus() = getResult(
        request = {
            api.getPalletStatuses()
        }
    )


    fun submitPalletShipping(
        pallets: List<PalletInShippingRow>,
    ) : Flow<BaseResult<ResultMessageModel>> {

        val jsonArray = JsonArray()

        pallets.map {
            val palletObject = JsonObject()
            palletObject.addProperty("ShippingID",it.shippingID)
            palletObject.addProperty("CustomerID",it.customerID)
            palletObject.addProperty("PalletTypeID",it.palletTypeID)
            palletObject.addProperty("PalletQuantity",it.palletQuantity)
            palletObject.addProperty("EntityState","Updated")
            palletObject
        }.forEach {
            jsonArray.add(it)
        }
        return getResult(
            request = {
                api.    submitShippingPallet(jsonArray)
            }
        )
    }

    fun getShippingPalletManifestList(warehouseID: Int) = getResult(
        request = {
            api.getShippingPalletManifestList(
                jsonObject = JsonObject().apply {
                    addProperty("Keyword", "")
                    addProperty("WarehouseID",warehouseID)
                },
                1,
                100,
                "CreatedOn",
                "desc"
            )
        }
    )

    fun addPalletToShipping(
        barcode: String
    ) = getResult(
        request = {
            api.addPalletToShipping(
                jsonObject = JsonObject().apply {
                    addProperty("PalletBarcode", barcode)
                }
            )
        }
    )

//    fun createShipping(
//        entities: List<ShippingPalletManifestRow>
//    ) = getResult(
//        request = {
//            val jsonArray = JsonArray()
//            entities.map {
//                val jsonObject = JsonObject()
//                jsonObject.addProperty("PalletManifestID", it.palletManifestId)
//                jsonObject
//            }.forEach {
//                jsonArray.add(it)
//            }
//            api.createShipping(
//                JsonObject().apply {
//                    add("PalletManifestEntities", jsonArray)
//                }
//            )
//        }
//    )

    fun getShippingDetailListOfPallet(
        palletManifestID: Int
    ) = getResult(
        request = {
            api.getShippingDetailListOfPallet(
                jsonObject = JsonObject().apply {
                    addProperty("PalletManifestID", palletManifestID)
                },
                1,
                100,
                "CreatedOn",
                "desc"
            )
        }
    )

    fun createShippingPallet(
        shippingID: Int,
        customerID: Int,
        palletTypeID: Int,
        palletStatusID: Int,
        palletQuantity: Double
    ) = getResult(
        request = {
            api.createShippingPallet(
                jsonObject = JsonObject().apply {
                    addProperty("ShippingID", shippingID)
                    addProperty("PartnerID", customerID)
                    addProperty("PalletTypeID", palletTypeID)
                    addProperty("PalletStatusID", palletStatusID)
                    addProperty("PalletQuantity", palletQuantity.toInt())
                }
            )
        }
    )

    fun updateShippingPallet(
        shippingPalletID: Int,
        quantity: Double
    ) = getResult(
        request = {
            api.updateShippingPallet(
                jsonObject = JsonObject().apply {
                    addProperty("ShippingPalletID", shippingPalletID)
                    addProperty("PalletQuantity", quantity.toInt())
                }
            )
        }
    )

    fun deleteShippingPallet(
        shippingPalletID: Int
    ) = getResult(
        request = {
            api.deleteShippingPallet(
                jsonObject = JsonObject().apply {
                    addProperty("ShippingPalletID", shippingPalletID)
                }
            )
        }
    )

    fun rollbackShipping(
        shippingId: Int
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("ShippingID",shippingId)
            api.shippingRollback(jsonObject)
        }
    )


    fun shippingPalletConfirm(
        shippingId: Int
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("ShippingID",shippingId)
            api.shippingPalletConfirm(jsonObject)
        }
    )

    fun getShipping(
        shippingId: Int,
        warehouseID: Int,
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("ShippingID",shippingId)
            jsonObject.addProperty("WarehouseID",warehouseID)
            api.getShipping(jsonObject)
        }
    )

    fun addPalletManifestToShipping(
        shippingId: Int,
        barcode: String
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("ShippingID",shippingId)
            jsonObject.addProperty("PalletBarcode",barcode)
            api.addPalletManifestToShipping(jsonObject)
        }
    )

    fun removePalletManifestToShipping(
        shippingId: Int,
        barcode: String
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("ShippingID",shippingId)
            jsonObject.addProperty("PalletBarcode",barcode)
            api.removePalletManifestFromShipping(jsonObject)
        }
    )
    fun getPalletProductList(
        palletManifestId: String,
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("Keyword","")
            jsonObject.addProperty("PalletManifestID",palletManifestId)
            api.getPalletManifestProduct(jsonObject,1,100,"","")
        }
    )

    fun getCustomerPalletIsNotInShipping(
        shippingId: Int,
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("ShippingID",shippingId)
            api.getCustomerPalletIsNotInShipping(jsonObject)
        }
    )
}