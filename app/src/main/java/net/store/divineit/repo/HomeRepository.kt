package net.store.divineit.repo

import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.store.divineit.api.ApiService
import net.store.divineit.models.AllProductsResponse
import net.store.divineit.models.LoginResponse
import net.store.divineit.models.SignUpResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val apiService: ApiService
    ) {

    suspend fun allProducts(): Response<AllProductsResponse> {
        return withContext(Dispatchers.IO) {
            apiService.allProducts()
        }
    }
}