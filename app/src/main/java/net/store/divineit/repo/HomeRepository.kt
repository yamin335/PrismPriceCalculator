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

    suspend fun myQuotations(email: String): Response<QuotationListResponse> {
        val jsonObject = JsonObject().apply {
            addProperty("email", email)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.myQuotations(1, jsonObject)
        }
    }

    suspend fun quotationDetails(quotationId: String): Response<QuotationDetailsResponse> {
        val jsonObject = JsonObject().apply {
            addProperty("quotationid", quotationId)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.quotationDetails(jsonObject)
        }
    }

    suspend fun quotationUpdate(summaryUpdateBody: SummaryResponseQuotation): Response<QuotationUpdateResponse> {
        val jsonObject = Gson().toJson(summaryUpdateBody)

        return withContext(Dispatchers.IO) {
            apiService.quotationUpdate(jsonObject)
        }
    }
}