package net.store.divineit.ui.base

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.store.divineit.R
import net.store.divineit.prefs.PreferencesHelper
import net.store.divineit.utils.NetworkUtils
import net.store.divineit.utils.showErrorToast
import javax.inject.Inject

abstract class BaseViewModel constructor(private val context: Context) : ViewModel() {

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    val apiCallStatus: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val toastError = MutableLiveData<String>()
    val toastWarning = MutableLiveData<String>()
    val toastSuccess = MutableLiveData<String>()
    val popBackStack = MutableLiveData<Boolean>()

    fun checkNetworkStatus(shouldShowMessage: Boolean) = when {
        NetworkUtils.isNetworkConnected(context) -> {
            true
        }
        shouldShowMessage -> {
            showErrorToast(context, context.getString(R.string.internet_error_msg))
            false
        }
        else -> {
            false
        }
    }
}