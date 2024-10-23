        package com.example.jaywarehouse.data.picking.models
import com.google.gson.annotations.SerializedName


data class PickedScanItemsModel(
    @SerializedName("Picking")
    val picking: ReadyToPickRow,
    @SerializedName("rows")
    val rows: List<PickedScanItemRow>,
    @SerializedName("total")
    val total: Int
)
        
data class PickingRow(
    @SerializedName("BrandName")
    val brandName: String?,
    @SerializedName("CreatedOn")
    val createdOn: String?,
    @SerializedName("LocationCode")
    val locationCode: String?,
    @SerializedName("ProductCode")
    val productCode: String?,
    @SerializedName("ProductGenderTitle")
    val productGenderTitle: String?,
    @SerializedName("ProductName")
    val productName: String?,
    @SerializedName("ProductSizeTitle")
    val productSizeTitle: String?,
    @SerializedName("ProductTypeTitle")
    val productTypeTitle: String?,
    @SerializedName("Quantity")
    val quantity: Int?,
    @SerializedName("ScanCount")
    val scanCount: Int?
)
data class PickedScanItemRow(
    @SerializedName("Date")
    val date: String,
    @SerializedName("PickingID")
    val pickingID: Int,
    @SerializedName("PickingScanID")
    val pickingScanID: Int,
    @SerializedName("Time")
    val time: String
)