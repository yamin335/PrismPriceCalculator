package net.store.divineit.ui.home

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import net.store.divineit.R
import net.store.divineit.api.ApiCallStatus
import net.store.divineit.models.LoginResponseData
import net.store.divineit.models.ServiceProduct
import net.store.divineit.repo.HomeRepository
import net.store.divineit.repo.LoginRepository
import net.store.divineit.ui.base.BaseViewModel
import net.store.divineit.utils.ApiEmptyResponse
import net.store.divineit.utils.ApiErrorResponse
import net.store.divineit.utils.ApiResponse
import net.store.divineit.utils.ApiSuccessResponse
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val application: Context,
    private val homeRepository: HomeRepository
) : BaseViewModel(application) {
    val allProducts: MutableLiveData<ArrayList<ServiceProduct>> by lazy {
        MutableLiveData<ArrayList<ServiceProduct>>()
    }

    fun loadAllProducts() {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(application.getString(R.string.commonErrorMessage))
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(homeRepository.allProducts())) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        when(apiResponse.body.code) {
                            2000001 -> {
                                apiResponse.body.data?.products?.let {
                                    allProducts.postValue(it)
                                }
                            }
                            else -> {
                                toastError.postValue("Failed! Please check your credentials and try again.")
                            }
                        }
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                        toastError.postValue("Failed! Please check your credentials and try again.")
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                        toastError.postValue("Failed! Please check your credentials and try again.")
                    }
                }
            }
        }
    }
}