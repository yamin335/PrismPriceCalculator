package net.store.divineit.ui.signup

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
import net.store.divineit.models.SignUpResponse
import net.store.divineit.models.SignUpResponseData
import net.store.divineit.repo.LoginRepository
import net.store.divineit.ui.base.BaseViewModel
import net.store.divineit.utils.ApiEmptyResponse
import net.store.divineit.utils.ApiErrorResponse
import net.store.divineit.utils.ApiResponse
import net.store.divineit.utils.ApiSuccessResponse
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    @ApplicationContext private val application: Context,
    private val loginRepository: LoginRepository
) : BaseViewModel(application) {

    val firstName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val lastName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val email: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val companyName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val mobileNumber: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val password: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val reTypePassword: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val signUpResponse: MutableLiveData<SignUpResponseData> by lazy {
        MutableLiveData<SignUpResponseData>()
    }

    fun signUp() {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(application.getString(R.string.commonErrorMessage))
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(loginRepository.signUp("${firstName.value}${lastName.value}",
                    password.value, email.value, 3, firstName.value, lastName.value, companyName.value, mobileNumber.value,
                    reTypePassword.value, "${firstName.value} ${lastName.value}", "customer", "customer"))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        when(apiResponse.body.code) {
                            2000001 -> {
                                signUpResponse.postValue(apiResponse.body.data)
                                toastSuccess.postValue("Account Created Successfully!")
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