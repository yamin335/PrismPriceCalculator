package net.store.divineit.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.store.divineit.BR
import net.store.divineit.R
import net.store.divineit.databinding.PricingActivityBinding
import net.store.divineit.ui.main.MainActivity
import net.store.divineit.ui.base.BaseActivity

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