package net.store.divineit.repo

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.store.divineit.api.ApiService
import net.store.divineit.models.*
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

    suspend fun submitQuotation(summaryStoreBody: SummaryStoreModel): Response<SummaryResponse> {
        val jsonObject = Gson().toJson(summaryStoreBody)

        return withContext(Dispatchers.IO) {
            apiService.submitQuotation(jsonObject)
        }
    }

    suspend fun productDetails(productId: String): Response<ServiceModuleResponse> {
        val jsonObject = JsonObject().apply {
            addProperty("productid", productId)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.productDetails(jsonObject)
        }
    }
}