package com.linari.data.loading

import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.common.utils.ResultMessageModel
import com.linari.data.common.utils.getResult
import com.linari.data.loading.models.LoadingListGroupedModel
import com.linari.data.loading.models.LoadingListGroupedRow
import com.linari.data.pallet.model.PalletConfirmModel
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class LoadingRepository(private val api: LoadingApi) {

    fun getLoadingListGrouped(
        keyword: String,
        page: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<LoadingListGroupedModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)

        return getResult(
            request = {
                api.getLoadingListGrouped(jsonObject,page,ROW_COUNT,sort,order)
            }
        )
    }


    fun getLoadingList(
        keyword: String,
        customerCode: String,
        page: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<PalletConfirmModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        jsonObject.addProperty("CustomerCode",customerCode)

        return getResult(
            request = {
                api.getLoadingList(jsonObject,page,ROW_COUNT,sort,order)
            }
        )
    }

    fun confirmLoading(
        palletManifestId: Int
    ) : Flow<BaseResult<ResultMessageModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("PalletManifestID",palletManifestId)

        return getResult(
            request = {
                api.confirmLoading(jsonObject)
            }
        )
    }
}