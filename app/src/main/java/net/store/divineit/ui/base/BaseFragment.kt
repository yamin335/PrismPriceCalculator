package net.store.divineit.ui.base

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import net.store.divineit.utils.autoCleared
import net.store.divineit.utils.showErrorToast
import net.store.divineit.utils.showSuccessToast
import net.store.divineit.utils.showWarningToast

abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel> : Fragment() {

    var binding by autoCleared<T>()

    private val navController: NavController
        get() = findNavController()

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract val br: Int

    /**
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    abstract val viewModel: V

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setVariable(br, viewModel)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()

        viewModel.toastError.observe(viewLifecycleOwner, {
            it?.let { msg ->
                showErrorToast(requireContext(), msg)
                viewModel.toastError.postValue(null)
            }
        })

        viewModel.toastWarning.observe(viewLifecycleOwner, {
            it?.let { msg ->
                showWarningToast(requireContext(), msg)
                viewModel.toastWarning.postValue(null)
            }
        })

        viewModel.toastSuccess.observe(viewLifecycleOwner, {
            it?.let { msg ->
                showSuccessToast(requireContext(), msg)
                viewModel.toastSuccess.postValue(null)
            }
        })
    }

    fun updateStatusBarBackgroundColor(color: String) {
        try {
            requireActivity().window.statusBarColor = Color.parseColor(color)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun navigate(direction: NavDirections) {
        try {
            navController.navigate(direction)
        } catch (e: Exception) {

        }
    }
}