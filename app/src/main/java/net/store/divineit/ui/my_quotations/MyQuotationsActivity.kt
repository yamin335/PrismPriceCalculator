package net.store.divineit.ui.my_quotations

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.store.divineit.BR
import net.store.divineit.R
import net.store.divineit.databinding.MyQuotationsActivityBinding
import net.store.divineit.ui.base.BaseActivity
import net.store.divineit.ui.login.LoginActivity
import net.store.divineit.ui.main.MainActivity
import net.store.divineit.utils.AppConstants.KEY_PRODUCT_ID
import net.store.divineit.utils.AppConstants.KEY_QUOTATION_ID

@AndroidEntryPoint
class MyQuotationsActivity : BaseActivity<MyQuotationsActivityBinding, MyQuotationsViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_my_quotations
    override val viewModel: MyQuotationsViewModel by viewModels()

    lateinit var myQuotationsListAdapter: MyQuotationsListAdapter

    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpToolbar()

        myQuotationsListAdapter = MyQuotationsListAdapter {
            val bundle = Bundle()
            bundle.putString(KEY_QUOTATION_ID, it.quotationid)
            bundle.putString(KEY_PRODUCT_ID, it.productid)

            val intent = Intent(this@MyQuotationsActivity, MainActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
//            PricingActivity.productId = it.id ?: ""
//            startActivity(Intent(this@MyQuotationsActivity, PricingActivity::class.java))
//            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        binding.recyclerQuotations.apply {
            adapter = myQuotationsListAdapter
        }

        viewModel.myQuotations.observe(this) {
            myQuotationsListAdapter.submitList(it)
        }

        preferencesHelper.userAccount?.email?.let {
            viewModel.loadMyQuotations(it)
        }
    }

    private fun setUpToolbar() {
        binding.toolbar.title = "Prism Price Calculator"
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (preferencesHelper.isLoggedIn) {
            menuInflater.inflate(R.menu.menu_only_logout, menu)
        } else {
            menuInflater.inflate(R.menu.menu_login, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.login -> {
                startActivity(Intent(this@MyQuotationsActivity, LoginActivity::class.java))
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                true
            }
            R.id.logout -> {
                preferencesHelper.logoutUser()
                invalidateOptionsMenu()
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