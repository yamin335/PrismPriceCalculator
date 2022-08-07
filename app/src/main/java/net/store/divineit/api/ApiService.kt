package net.store.divineit.api

import net.store.divineit.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

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
}
