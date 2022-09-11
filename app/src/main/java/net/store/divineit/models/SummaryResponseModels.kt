package net.store.divineit.models

data class QuotationUpdateResponse(
    val code: Int?,
    val data: QuotationUpdateResponseData?,
    val msg: String?
)

data class QuotationUpdateResponseData(
    val Quotation: SummaryResponseQuotation?
)

data class QuotationDetailsResponse(
    val code: Int?, 
    val data: QuotationDetailsResponseData?, 
    val msg: String?
)

data class QuotationDetailsResponseData(
    val QuotationSummary: SummaryResponseQuotation?
)

data class SummaryResponse(
    val code: Int?,
    val data: SummaryResponseData?,
    val msg: String?
)

data class SummaryResponseData(
    val quotation: SummaryResponseQuotation?
)

data class SummaryResponseQuotation(
    val id: Int?,
    val status: String?,
    val ishistory: Boolean?,
    val quotationhid: String?,
    val header: String?,
    val customerid: Int?,
    val salesmanid: Int?,
    val quotationid: String?,
    val productid: String?,
    val company: String?,
    var totalamount: Int?,
    val discount: Int?,
    var Software_License: SummaryResponseSoftwareLicense?,
    var Implementation: SummaryResponseAdditionalService?,
    var Customization: SummaryResponseAdditionalService?,
    var Consultancy: SummaryResponseAdditionalService?,
    var Maintainance: SummaryResponseAdditionalService?,
    val Table: String?
)

data class SummaryResponseAdditionalService(
    val summeryid: String?,
    val header: String?,
    val total: Int?,
    val discount: Int?,
    val modules: ArrayList<SummaryResponseAdditionalServiceModule>,
    val Table: String?
)

data class SummaryResponseAdditionalServiceModule(
    val summeryid: String?,
    val totalamount: Int?,
    val discount: Int?,
    val name: String?,
    val details: String?,
    val details_value: Int?,
    val details_multiplier: Int?,
    val Table: String?
)

data class SummaryResponseSoftwareLicense(
    val summeryid: String?,
    val header: String?,
    val totalamount: Int?,
    val discount: Int?,
    val users: Int?,
    val additionalusers: Int?,
    val modules: ArrayList<SummaryResponseSoftwareLicenseModule>,
    val Table: String?
)

data class SummaryResponseSoftwareLicenseModule(
    val licensingparameters: ArrayList<LicensingParameter>?,
    val name: String?,
    val code: String?,
    val description: String?,
    val selfcode: String?,
    val defaultprice: Int?,
    val totalamount: Int?,
    val discount: Int?,
    val features: ArrayList<SummaryResponseFeature>,
    val multiplier: String?,
    val price: Int?,
    val excludeInAll: Boolean?
)

data class SummaryResponseFeature(
    val name: String?,
    val code: String?,
    val parentcode: String?,
    val description: String?,
    val multipliercode: String?,
    val type: String?,
    val excludeInAll: Boolean?,
    val discount: Int?,
    val totalamount: Int?,
    val multiplier: String?,
    val price: List<String>?,
    val defaultprice: Double = 0.0
)