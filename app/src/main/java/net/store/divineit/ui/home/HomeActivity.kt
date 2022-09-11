package net.store.divineit.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.store.divineit.BR
import net.store.divineit.R
import net.store.divineit.databinding.HomeActivityBinding
import net.store.divineit.ui.AllProductsListAdapter
import net.store.divineit.ui.base.BaseActivity
import net.store.divineit.ui.login.LoginActivity
import net.store.divineit.ui.main.MainActivity
import net.store.divineit.ui.my_quotations.MyQuotationsActivity

@AndroidEntryPoint
class HomeActivity : BaseActivity<HomeActivityBinding, HomeViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_home
    override val viewModel: HomeViewModel by viewModels()

    lateinit var serviceAdapter: AllProductsListAdapter

    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpToolbar()

        serviceAdapter = AllProductsListAdapter {
            PricingActivity.productId = it.id ?: ""
            startActivity(Intent(this@HomeActivity, PricingActivity::class.java))
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        binding.recyclerService.apply {
            adapter = serviceAdapter
        }

        viewModel.allProducts.observe(this) {
            serviceAdapter.submitList(it)
        }

        viewModel.loadAllProducts()
    }

    private fun setUpToolbar() {
        binding.toolbar.title = "Prism Price Calculator"
        setSupportActionBar(binding.toolbar)
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
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                true
            }
            R.id.logout -> {
                preferencesHelper.logoutUser()
                invalidateOptionsMenu()
                true
            }
            R.id.myQuotations -> {
                startActivity(Intent(this@HomeActivity, MyQuotationsActivity::class.java))
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}