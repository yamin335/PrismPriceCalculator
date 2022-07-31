package net.store.divineit.models

data class AllProductsResponse(
    val code: Int?,
    val data: AllProductsResponseData?,
    val msg : String?
)

// MARK: - DataClass
data class AllProductsResponseData(
    val products: ArrayList<ServiceProduct>
)

// MARK: - Product
data class ServiceProduct(
    val id: String?,
    val name: String?,
    val logo: String?,
    val title: String?,
    val description: String?,
    val sheetlink: String?,
    val productdetailslink: String?,
    val isactive: Boolean?,
    val price: Int?,
    val Table: String?,
)