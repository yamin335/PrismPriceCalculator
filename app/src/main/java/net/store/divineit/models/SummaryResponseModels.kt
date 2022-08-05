package net.store.divineit.models

data class SummaryResponse (
    val code: Int?,
    val data: SummaryResponseData?,
    val msg: String?
)

data class SummaryResponseData (
    val quotation: SummaryResponseQuotation?
)

data class SummaryResponseQuotation (
    val id: Int?,
    val status: String?,
    val ishistory: Boolean?,
    val quotationhid: String?,
    val header: String?,
    val customerid: Int?,
    val salesmanid: Int?,
    val quotationid: String?,
    val productid: String?,
    val totalamount: Int?,
    val discount: Int?,
    val softwareLicense: SummaryResponseSoftwareLicense?,
    val implementation: SummaryResponseConsultancy?,
    val customization: SummaryResponseConsultancy?,
    val consultancy: SummaryResponseConsultancy?,
    val maintainance: SummaryResponseConsultancy?,
    val table: String?
)

data class SummaryResponseConsultancy (
    val summeryid: String?,
    val header: String?,
    val total: Int?,
    val discount: Int?,
    val modules: ArrayList<SummaryResponseConsultancyModule>,
    val table: String?
)

data class SummaryResponseConsultancyModule (
    val summeryid: String?,
    val totalamount: Int?,
    val discount: Int?,
    val name: String?,
    val details: String?,
    val detailsValue: Int?,
    val detailsMultiplier: Int?,
    val table: String?
)

data class SummaryResponseSoftwareLicense (
    val summeryid: String?,
    val header: String?,
    val totalamount: Int?,
    val discount: Int?,
    val users: Int?,
    val additionalusers: Int?,
    val modules: ArrayList<SummaryResponseSoftwareLicenseModule>,
    val table: String?
)

data class SummaryResponseSoftwareLicenseModule (
    val name: String?,
    val code: String?,
    val selfcode: String?,
    val price: Int?,
    val totalamount: Int?,
    val discount: Int?,
    val features: ArrayList<SummaryResponseFeature>
)

data class SummaryResponseFeature (
    val name: String?,
    val code: String?,
    val parentcode: String?,
    val featureDescription: String?,
    val multipliercode: String?,
    val type: String?,
    val discount: Int?,
    val price: Int?
)