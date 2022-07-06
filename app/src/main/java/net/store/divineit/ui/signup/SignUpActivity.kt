package net.store.divineit.ui.signup

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.store.divineit.BR
import net.store.divineit.R
import net.store.divineit.databinding.SignUpActivityBinding
import net.store.divineit.ui.base.BaseActivity
import net.store.divineit.utils.isValidEmail
import net.store.divineit.utils.isValidPassword

@AndroidEntryPoint
class SignUpActivity : BaseActivity<SignUpActivityBinding, SignUpViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_signup
    override val viewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpToolbar()

        binding.btnSignIn.setOnClickListener {
            goBack()
        }

        viewModel.firstName.observe(this) {
            binding.btnSignUp.isEnabled = isAllDataValid()
        }

        viewModel.lastName.observe(this) {
            binding.btnSignUp.isEnabled = isAllDataValid()
        }

        viewModel.email.observe(this) {
            binding.btnSignUp.isEnabled = isAllDataValid()
        }

        viewModel.companyName.observe(this) {
            binding.btnSignUp.isEnabled = isAllDataValid()
        }

        viewModel.mobileNumber.observe(this) {
            binding.btnSignUp.isEnabled = isAllDataValid()
        }

        viewModel.password.observe(this) {
            binding.btnSignUp.isEnabled = isAllDataValid()
        }

        viewModel.reTypePassword.observe(this) {
            binding.btnSignUp.isEnabled = isAllDataValid()
        }

        viewModel.signUpResponse.observe(this) {
            goBack()
        }
    }

    private fun isAllDataValid(): Boolean {
        return !viewModel.firstName.value.isNullOrBlank()
                && !viewModel.lastName.value.isNullOrBlank()
                && viewModel.email.value.isValidEmail()
                && !viewModel.companyName.value.isNullOrBlank()
                && !viewModel.mobileNumber.value.isNullOrBlank()
                && viewModel.password.value.isValidPassword()
                && viewModel.reTypePassword.value.isValidPassword()
                && viewModel.password.value == viewModel.reTypePassword.value
    }

    private fun setUpToolbar() {
        binding.toolbar.title = "Create New Account"
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                goBack()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun goBack() {
        finish()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}