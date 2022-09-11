package net.store.divineit.models

data class SummaryStoreModel (
    val salesmanid: Int?,
    val customerid: Int?,
    val details: Boolean?,
    val header: String?,
    val productid: String?,
    val totalamount: Int?,
    val Software_License: SummaryResponseSoftwareLicense?,
    val Implementation: SummaryResponseAdditionalService?,
    val Customization: SummaryResponseAdditionalService?,
    val Consultancy: SummaryResponseAdditionalService?,
    val Maintainance: SummaryResponseAdditionalService?,
    val company: String
)

data class LicensingParameter (
    val name: String?,
    val value: String?,
    val slabid: Int?
)