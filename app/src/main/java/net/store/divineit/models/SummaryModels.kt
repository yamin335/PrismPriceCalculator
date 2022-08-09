package net.store.divineit.models

data class SummaryStoreModel (
    val salesmanid: Int?,
    val customerid: Int?,
    val details: Boolean?,
    val header: String?,
    val productid: String?,
    val totalamount: Int?,
    val Software_License: SummarySoftwareLicense?,
    val Implementation: SummaryService?,
    val Customization: SummaryService?,
    val Consultancy: SummaryService?,
    val Maintainance: SummaryService?,
    val company: String
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
    val licensingparameters: ArrayList<LicensingParameter>,
    val features: ArrayList<SummaryModuleFeature>
)

data class SummaryModuleFeature (
    val code: String?,
    val multiplier: String?,
    val multipliercode: String?,
    val price: List<String>,
    val type: String?,
    val defaultprice: Int?
)

data class SummaryService (
    val header: String?,
    val totalamount: Int?,
    val modules: ArrayList<SummaryServiceModule>
)

data class SummaryServiceModule (
    val name: String?,
    val details: String?,
    val details_value: Int?,
    val details_multiplier: Int?,
    val totalamount: Int?
)

data class LicensingParameter (
    val name: String?,
    val value: String?,
    val slabid: Int?
)