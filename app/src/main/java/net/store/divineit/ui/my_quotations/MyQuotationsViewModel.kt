package net.store.divineit.ui.my_quotations

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
import net.store.divineit.models.MyQuotation
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
class MyQuotationsViewModel @Inject constructor(
    @ApplicationContext private val application: Context,
    private val homeRepository: HomeRepository
) : BaseViewModel(application) {

    val myQuotations: MutableLiveData<List<MyQuotation>> by lazy {
        MutableLiveData<List<MyQuotation>>()
    }

    fun loadMyQuotations(email: String) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(application.getString(R.string.commonErrorMessage))
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(homeRepository.myQuotations(email))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        when(apiResponse.body.code) {
                            2000001 -> {
                                apiResponse.body.data?.quotations?.let {
                                    myQuotations.postValue(it)
                                }
                            }
                            else -> {
                                toastError.postValue("Failed! to load your quotations")
                            }
                        }
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                        toastError.postValue("No quotations found")
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                        toastError.postValue("Failed! to load your quotations")
                    }
                }
            }
        }
    }
}