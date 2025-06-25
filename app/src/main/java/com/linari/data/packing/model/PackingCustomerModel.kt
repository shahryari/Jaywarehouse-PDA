package com.linari.data.packing.model
import com.google.gson.annotations.SerializedName



class PackingCustomerModel : ArrayList<PackingCustomerModelItem>()

data class PackingCustomerModelItem(
    @SerializedName("CustomerCode")
    val customerCode: String,
    @SerializedName("CustomerID")
    val customerID: Int,
    @SerializedName("CustomerName")
    val customerName: String
) {
    override fun toString(): String {
        return customerName
    }
}