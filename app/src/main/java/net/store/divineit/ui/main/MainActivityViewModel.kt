package net.store.divineit.ui.main

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
class MainActivityViewModel @Inject constructor(
    @ApplicationContext private val application: Context,
    private val loginRepository: LoginRepository
) : BaseViewModel(application) {
    var selectedMultiplier: Pair<Int, String> = Pair(0, "")
//    val selectedMultiplier: MutableLiveData<Pair<Int, String>> by lazy {
//        MutableLiveData<Pair<Int, String>>()
//    }
}