package net.store.divineit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import net.store.divineit.databinding.ActivityMainBinding
import net.store.divineit.models.BaseServiceModule
import net.store.divineit.models.FinancialService
import net.store.divineit.ui.BaseServiceModuleListAdapter
import net.store.divineit.ui.FinancialServiceListAdapter
import java.io.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: FinancialServiceListAdapter
    private lateinit var summarySheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var baseServiceAdapter: BaseServiceModuleListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inputStream: InputStream = resources.openRawResource(R.raw.divine)
        val writer: Writer = StringWriter()
        val buffer = CharArray(1024)
        inputStream.use { stream ->
            val reader: Reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
            var n: Int
            while (reader.read(buffer).also { n = it } != -1) {
                writer.write(buffer, 0, n)
            }
        }

        val jsonString: String = writer.toString()
        val baseModuleList = Gson().fromJson(jsonString, Array<BaseServiceModule>::class.java).toList()

        baseServiceAdapter = BaseServiceModuleListAdapter {

        }
        baseServiceAdapter.submitList(baseModuleList)
        binding.baseServiceModuleRecycler.adapter = baseServiceAdapter

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