package net.store.divineit.ui.login

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
import net.store.divineit.repo.LoginRepository
import net.store.divineit.ui.base.BaseViewModel
import net.store.divineit.utils.ApiEmptyResponse
import net.store.divineit.utils.ApiErrorResponse
import net.store.divineit.utils.ApiResponse
import net.store.divineit.utils.ApiSuccessResponse
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val application: Context,
    private val loginRepository: LoginRepository
) : BaseViewModel(application) {

    val email: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val password: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val loginResponse: MutableLiveData<LoginResponseData> by lazy {
        MutableLiveData<LoginResponseData>()
    }

    fun login() {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(application.getString(R.string.commonErrorMessage))
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(loginRepository.login(email.value, password.value))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        when(apiResponse.body.code) {
                            2000001 -> {
                                loginResponse.postValue(apiResponse.body.data)
                                toastSuccess.postValue("Login Successful!")
                            }
                            else -> {
                                toastError.postValue("Login Failed! Please check your credentials and try again.")
                            }
                        }
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                        toastError.postValue("Login Failed! Please check your credentials and try again.")
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                        toastError.postValue("Login Failed! Please check your credentials and try again.")
                    }
                }
            }
        }
    }
}