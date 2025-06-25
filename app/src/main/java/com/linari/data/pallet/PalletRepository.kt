package com.linari.data.pallet

import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.common.utils.ResultMessageModel
import com.linari.data.common.utils.getResult
import com.linari.data.pallet.model.PalletConfirmModel
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class PalletRepository(
    private val api: PalletApi
) {
    fun getPalletList(
        keyword: String,
        page: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<PalletConfirmModel>>{
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        return getResult(
            request = {
                api.getPalletManifestList(
                    jsonObject,
                    page,
                    ROW_COUNT,
                    sort,
                    order
                )
            }
        )
    }

    fun completePalletManifest(
        palletManifestId: String
    ) : Flow<BaseResult<ResultMessageModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("PalletManifestID",palletManifestId)
        return getResult(
            request = {
                api.completePalletManifest(jsonObject)
            }
        )
    }

    fun getPalletProductList(
        keyword: String,
        palletManifestId: String,
        page: Int,
        sort: String,
        order: String
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("Keyword",keyword)
            jsonObject.addProperty("PalletManifestID",palletManifestId)
            api.getPalletManifestProduct(jsonObject,page,ROW_COUNT,sort,order)
        }
    )
}