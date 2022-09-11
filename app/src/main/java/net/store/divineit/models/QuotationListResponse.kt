package net.store.divineit.models

data class QuotationListResponse(val code: Int?, val data: QuotationListResponseData?, val msg: String?)

data class QuotationListResponseData(val pagination: PaginationData?, val quotations: List<MyQuotation>?)

data class PaginationData(val total_page: Int?, val offset: Int?, val limit: Int?, val page: Int?)

data class MyQuotation(val quotationid: String?, val productid: String?,
                                val customerid: String?, val salesmanid: Int?,
                                val customername: String?, val status: String?,
                                val totalamount: Int?, val discount: Int?,
                                val company: String?, val customercompany: String?,
                                val customermobile: String?, val date: String?)
