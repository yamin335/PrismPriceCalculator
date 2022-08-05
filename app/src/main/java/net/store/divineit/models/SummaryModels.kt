package net.store.divineit.models

data class SummaryStoreModel (
    val salesmanid: Int?,
    val customerid: Int?,
    val details: Boolean?,
    val header: String?,
    val productid: String?,
    val totalamount: Int?,
    val softwareLicense: SummarySoftwareLicense?,
    val implementation: SummaryService?,
    val customization: SummaryService?,
    val consultancy: SummaryService?,
    val maintainance: SummaryService?
)

data class SummarySoftwareLicense (
    val additionalusers: Int?,
    val users: Int?,
    val header: String?,
    val totalamount: Int?,
    val modules: ArrayList<SoftwareLicenseModule>
)

data class SoftwareLicenseModule (
    val name: String?,
    val totalamount: Int?,
    val code: String?,
    val features: ArrayList<SummaryModuleFeature>
)

data class SummaryModuleFeature (
    val code: String?,
    val multipliercode: String?,
    val price: Int?,
    val prices: Price?,
    val type: String?
)

data class SummaryService (
    val header: String?,
    val totalamount: Int?,
    val modules: ArrayList<SummaryServiceModule>
)

data class SummaryServiceModule (
    val name: String?,
    val details: String?,
    val detailsValue: Int?,
    val detailsMultiplier: Int?,
    val totalamount: Int?
)