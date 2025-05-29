package com.example.jaywarehouse.data.pallet

import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.ROW_COUNT
import com.example.jaywarehouse.data.common.utils.ResultMessageModel
import com.example.jaywarehouse.data.common.utils.getResult
import com.example.jaywarehouse.data.pallet.model.PalletConfirmModel
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
}