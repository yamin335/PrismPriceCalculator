package net.store.divineit.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.store.divineit.BR
import net.store.divineit.R
import net.store.divineit.databinding.LoginActivityBinding
import net.store.divineit.ui.signup.SignUpActivity
import net.store.divineit.ui.base.BaseActivity
import net.store.divineit.utils.isValidEmail
import net.store.divineit.utils.isValidPassword

@AndroidEntryPoint
class LoginActivity : BaseActivity<LoginActivityBinding, LoginViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_login
    override val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpToolbar()

        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        viewModel.email.observe(this) {
            binding.btnSignIn.isEnabled = viewModel.password.value.isValidPassword() && it.isValidEmail()
        }

        viewModel.password.observe(this) {
            binding.btnSignIn.isEnabled = viewModel.email.value.isValidEmail() && it.isValidPassword()
        }

        viewModel.loginResponse.observe(this) {
            preferencesHelper.isLoggedIn = true
            preferencesHelper.loginToken = it.Token
            preferencesHelper.userAccount = it.Account
            goBack()
        }
    }

    private fun setUpToolbar() {
        binding.toolbar.title = "Account Log In"
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