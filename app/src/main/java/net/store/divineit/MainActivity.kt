package net.store.divineit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import net.store.divineit.databinding.ActivityMainBinding
import net.store.divineit.models.FinancialService
import net.store.divineit.ui.FinancialServiceListAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: FinancialServiceListAdapter
    private lateinit var summarySheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        summarySheetBehavior = BottomSheetBehavior.from(binding.summarySheet.root)

        binding.summarySheet.btnClose.setOnClickListener {
            if(summarySheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                summarySheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                summarySheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        summarySheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Do something for new state.
                if(newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.summarySheet.btnClose.setImageResource(R.drawable.ic_baseline_clear_24)
                    binding.summarySheet.slider.visibility = View.INVISIBLE
                } else {
                    binding.summarySheet.btnClose.setImageResource(R.drawable.ic_baseline_open_in_full_24)
                    binding.summarySheet.slider.visibility = View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Do something for slide offset.
            }
        })

        adapter = FinancialServiceListAdapter {

        }

        binding.recyclerView.adapter = adapter

        binding.recyclerView.setHasFixedSize(true)

        adapter.submitList(arrayListOf(
            FinancialService("Financial Accounting", 10,
                0, false, arrayListOf("Chart of Accounts",
                    "Vouchers (Journal Voucher)", "Multi-year transaction history",
                    "Special Ledger Operation")),
            FinancialService("Cost Accounting", 13,
                0, false, arrayListOf("Chart of Accounts",
                    "Vouchers (Journal Voucher)", "Multi-year transaction history",
                    "Special Ledger Operation")),
            FinancialService("Project Accounting", 9,
                0, false, arrayListOf("Chart of Accounts",
                    "Vouchers (Journal Voucher)", "Multi-year transaction history",
                    "Special Ledger Operation")),
            FinancialService("Managerial Accounting", 17,
                0, false, arrayListOf("Chart of Accounts",
                    "Vouchers (Journal Voucher)", "Multi-year transaction history",
                    "Special Ledger Operation"))
        ))
    }
}