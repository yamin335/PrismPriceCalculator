package net.store.divineit.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.store.divineit.BR
import net.store.divineit.R
import net.store.divineit.databinding.PricingActivityBinding
import net.store.divineit.ui.main.MainActivity
import net.store.divineit.ui.base.BaseActivity
import net.store.divineit.ui.login.LoginActivity
import net.store.divineit.ui.my_quotations.MyQuotationsActivity

@AndroidEntryPoint
class PricingActivity : BaseActivity<PricingActivityBinding, PricingViewModel>() {
    companion object {
        var productId: String = ""
    }
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_pricing
    override val viewModel: PricingViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpToolbar()

        binding.btnCustomize.setOnClickListener {
            MainActivity.productId = productId
            startActivity(Intent(this@PricingActivity, MainActivity::class.java))
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }
    }

    private fun setUpToolbar() {
        binding.toolbar.title = "Customize Pricing"
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (preferencesHelper.isLoggedIn) {
            menuInflater.inflate(R.menu.menu_logout, menu)
        } else {
            menuInflater.inflate(R.menu.menu_login, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.login -> {
                startActivity(Intent(this@PricingActivity, LoginActivity::class.java))
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                true
            }
            R.id.logout -> {
                preferencesHelper.logoutUser()
                invalidateOptionsMenu()
                true
            }
            R.id.myQuotations -> {
                startActivity(Intent(this@PricingActivity, MyQuotationsActivity::class.java))
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                true
            }
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