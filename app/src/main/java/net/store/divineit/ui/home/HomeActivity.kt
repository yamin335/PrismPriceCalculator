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
import net.store.divineit.models.BusinessService
import net.store.divineit.ui.BusinessServiceListAdapter
import net.store.divineit.ui.base.BaseActivity
import net.store.divineit.ui.login.LoginActivity

@AndroidEntryPoint
class HomeActivity : BaseActivity<HomeActivityBinding, HomeViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_home
    override val viewModel: HomeViewModel by viewModels()

    lateinit var serviceAdapter: BusinessServiceListAdapter

    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpToolbar()

        val listOfServices = listOf(
            BusinessService("ERP", "https://prismerp.rtchubs.com/img/prismerp.png",
                "An ERP System to cover all business needs designed for medium and large business",
                2500000),
            BusinessService("ERPJ", "https://prismerp.rtchubs.com/img/J-Series.png",
            "PrismERP J Series designed for Multi-National & Government Authorities",
            0),
            BusinessService("ONEBOOK", "https://prismerp.rtchubs.com/img/Onebook_zfhzCpe.png",
                "Intelligent Business Assistant for Small and Medium Businesses",
                2000))

        serviceAdapter = BusinessServiceListAdapter(listOfServices) {
            startActivity(Intent(this@HomeActivity, PricingActivity::class.java))
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        binding.recyclerService.apply {
            adapter = serviceAdapter
        }

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
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}