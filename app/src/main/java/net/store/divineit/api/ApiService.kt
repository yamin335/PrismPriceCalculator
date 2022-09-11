package net.store.divineit.api

import net.store.divineit.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * REST API access points
 */
interface ApiService {
    @POST(ApiEndPoint.LOGIN)
    suspend fun login(@Body jsonString: String): Response<LoginResponse>

    @POST(ApiEndPoint.SIGNUP)
    suspend fun signUp(@Body jsonString: String): Response<SignUpResponse>

    @POST(ApiEndPoint.ALL_PRODUCTS)
    suspend fun allProducts(): Response<AllProductsResponse>

    @POST(ApiEndPoint.QUOTATION_SUBMIT)
    suspend fun submitQuotation(@Body jsonString: String): Response<SummaryResponse>

    @POST(ApiEndPoint.PRODUCT_DETAILS)
    suspend fun productDetails(@Body jsonString: String): Response<ServiceModuleResponse>

    @POST(ApiEndPoint.MY_QUOTATIONS)
    suspend fun myQuotations(@Query("page") page: Int, @Body jsonString: String): Response<QuotationListResponse>

    @POST(ApiEndPoint.QUOTATION_DETAILS)
    suspend fun quotationDetails(@Body jsonString: String): Response<QuotationDetailsResponse>

    @POST(ApiEndPoint.QUOTATION_UPDATE)
    suspend fun quotationUpdate(@Body jsonString: String): Response<QuotationUpdateResponse>
}
