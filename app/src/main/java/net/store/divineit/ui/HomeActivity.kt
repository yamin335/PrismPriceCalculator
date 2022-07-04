package net.store.divineit.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.GravityCompat
import net.store.divineit.R
import net.store.divineit.databinding.ActivityHomeBinding
import net.store.divineit.models.BusinessService

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    lateinit var serviceAdapter: BusinessServiceListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            startActivity(Intent(this@HomeActivity, MainActivity::class.java))
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_login, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.login -> {
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}