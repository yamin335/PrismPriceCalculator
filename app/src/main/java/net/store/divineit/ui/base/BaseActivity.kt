package net.store.divineit.ui.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.store.divineit.R
import net.store.divineit.application.AppExecutors
import net.store.divineit.databinding.LoginActivityBinding
import net.store.divineit.prefs.PreferencesHelper
import net.store.divineit.utils.showErrorToast
import net.store.divineit.utils.showSuccessToast
import net.store.divineit.utils.showWarningToast
import javax.inject.Inject

abstract class BaseActivity<T : ViewDataBinding, V : BaseViewModel> : AppCompatActivity() {

    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    /**
     * @return view data binding instance
     */
    lateinit var binding: T

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    abstract val viewModel: V

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract val bindingVariable: Int

    /**
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, layoutId)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.setVariable(bindingVariable, viewModel)

        viewModel.toastError.observe(this) {
            it?.let { msg ->
                showErrorToast(this, msg)
                viewModel.toastError.postValue(null)
            }
        }

        viewModel.toastWarning.observe(this) {
            it?.let { msg ->
                showWarningToast(this, msg)
                viewModel.toastWarning.postValue(null)
            }
        }

        viewModel.toastSuccess.observe(this) {
            it?.let { msg ->
                showSuccessToast(this, msg)
                viewModel.toastSuccess.postValue(null)
            }
        }
    }
}