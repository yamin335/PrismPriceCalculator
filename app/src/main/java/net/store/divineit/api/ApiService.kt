package net.store.divineit.api

import net.store.divineit.models.AllProductsResponse
import net.store.divineit.models.LoginResponse
import net.store.divineit.models.ServiceProduct
import net.store.divineit.models.SignUpResponse
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

    @POST(ApiEndPoint.ALL_Product)
    suspend fun allProducts(): Response<AllProductsResponse>
}