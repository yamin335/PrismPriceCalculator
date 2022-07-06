package net.store.divineit.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.store.divineit.BR
import net.store.divineit.R
import net.store.divineit.databinding.MainActivityBinding
import net.store.divineit.models.BaseServiceModule
import net.store.divineit.models.ModuleGroupSummary
import net.store.divineit.ui.BaseServiceModuleListAdapter
import net.store.divineit.ui.ModuleGroupAdapter
import net.store.divineit.ui.ModuleGroupSummaryListAdapter
import net.store.divineit.ui.base.BaseActivity
import net.store.divineit.ui.home.PricingViewModel
import net.store.divineit.ui.login.LoginActivity
import java.io.*

@AndroidEntryPoint
class MainActivity : BaseActivity<MainActivityBinding, MainActivityViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_main
    override val viewModel: MainActivityViewModel by viewModels()

    private lateinit var moduleGroupAdapter: ModuleGroupAdapter
    private lateinit var summarySheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var baseServiceAdapter: BaseServiceModuleListAdapter
    //private lateinit var mDrawerToggle: ActionBarDrawerToggle
    private lateinit var mDetector: GestureDetector
    private lateinit var moduleSummaryAdapter: ModuleGroupSummaryListAdapter
    private var selectedBaseModulePosition = 0
    private lateinit var baseModuleList: List<BaseServiceModule>
    private var summaryMap: HashMap<String, ModuleGroupSummary> = HashMap()

    private var costSoftwareLicense = 0
    private var costAdditionalUsers = 0
    private var costImplementation = 0
    private var costRequirementAnalysis = 0
    private var costDeployment = 0
    private var costConfiguration = 0
    private var costOnsiteAdoptionSupport = 0
    private var costTraining = 0
    private var costProjectManagement = 0
    private var costSoftwareCustomizationTotal = 0
    private var costSoftwareCustomization = 0
    private var costCustomizedReport = 0
    private var costConsultancyServices = 0
    private var costConsultancy = 0
    private var costAnnualMaintenanceTotal = 0
    private var costAnnualMaintenance = 0
    private var costTotal = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpToolbar()

        costAdditionalUsers = 150000
        costDeployment = 10000
        costAnnualMaintenance = 30000

        calculateSummaryCost(0)
        bindSummaryCostDataToUI()

        mDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent?): Boolean {
                if(summarySheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    summarySheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                } else {
                    summarySheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
                return true
            }
        })

//        mDrawerToggle = object : ActionBarDrawerToggle(this, binding.drawerLayout, binding.appBarMain.toolbar,
//            R.string.open,
//            R.string.close
//        ) {
//            override fun onDrawerClosed(drawerView: View) {
//                super.onDrawerClosed(drawerView)
//            }
//
//            override fun onDrawerOpened(drawerView: View) {
//                super.onDrawerOpened(drawerView)
//            }
//        }
//        binding.drawerLayout.addDrawerListener(mDrawerToggle)

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
        baseModuleList = Gson().fromJson(jsonString, Array<BaseServiceModule>::class.java).toList()

        if (baseModuleList.isEmpty()) return

        moduleSummaryAdapter = ModuleGroupSummaryListAdapter()
        binding.appBarMain.contentMain.summarySheet.recyclerSummary.adapter = moduleSummaryAdapter

        moduleGroupAdapter = ModuleGroupAdapter(baseModuleList[0].code, baseModuleList[0].moduleGroups) {
            calculateSummary()
        }

        val innerLLM = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        innerLLM.initialPrefetchItemCount = 3

        binding.appBarMain.contentMain.recyclerView.apply {
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
            layoutManager = innerLLM
            adapter = moduleGroupAdapter
        }

        baseServiceAdapter = BaseServiceModuleListAdapter(baseModuleList) { baseModule, position ->
            changeHeader(baseModule.code)
            selectedBaseModulePosition = position
            binding.drawerLayout.closeDrawer(GravityCompat.END)
            CoroutineScope(Dispatchers.Main.immediate).launch {
                delay(250)
                moduleGroupAdapter = ModuleGroupAdapter(baseModule.code, baseModule.moduleGroups) {
                    calculateSummary()
                }
                binding.appBarMain.contentMain.recyclerView.apply {
                    adapter = moduleGroupAdapter
                }
            }
        }
        baseServiceAdapter.submitList(baseModuleList)
        binding.baseServiceModuleRecycler.adapter = baseServiceAdapter

        changeHeader(baseModuleList[0].code)

        summarySheetBehavior = BottomSheetBehavior.from(binding.appBarMain.contentMain.summarySheet.root)

        binding.appBarMain.contentMain.summarySheet.btnClose.setOnClickListener {
            if(summarySheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                summarySheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                summarySheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        binding.appBarMain.contentMain.summarySheet.summarySheetRootView.setOnTouchListener { _, motionEvent ->
            return@setOnTouchListener mDetector.onTouchEvent(motionEvent)
        }

        summarySheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Do something for new state.
                if(newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.appBarMain.contentMain.summarySheet.btnClose.setImageResource(R.drawable.ic_baseline_clear_24)
                    binding.appBarMain.contentMain.summarySheet.slider.visibility = View.INVISIBLE
                } else {
                    binding.appBarMain.contentMain.summarySheet.btnClose.setImageResource(R.drawable.ic_baseline_open_in_full_24)
                    binding.appBarMain.contentMain.summarySheet.slider.visibility = View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Do something for slide offset.
            }
        })

        binding.appBarMain.contentMain.summarySheet.btnSubmit.setOnClickListener {
            if (preferencesHelper.isLoggedIn) {
                viewModel.toastSuccess.postValue("Submitted Successfully!")
            } else {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            }
        }
    }

    private fun setUpToolbar() {
        binding.appBarMain.toolbar.title = "Prism Price Calculator"
        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun calculateSummary() {
        val baseModule = baseModuleList[selectedBaseModulePosition]
        var isAdded = false
        var price = 0
        for (moduleGroup in baseModule.moduleGroups) {
            for (module in moduleGroup.modules) {
                if (module.isAdded) {
                    val slab1 = module.price?.slab1
                    if (slab1 != null) {
                        try {
                            price += slab1.toInt()
                        } catch (e: Exception) {
                            continue
                        }
                    }
                    isAdded = true
                }

                for (feature in module.features) {
                    if (feature.isAdded) {
                        val slab1 = feature.price?.slab1
                        if (slab1 != null) {
                            try {
                                price += slab1.toInt()
                            } catch (e: Exception) {
                                continue
                            }
                        }
                        isAdded = true
                    }
                }

                for (subModule in module.submodules) {
                    val features = subModule.features
                    for (feature in features) {
                        if (feature.isAdded) {
                            val slab1 = feature.price?.slab1
                            if (slab1 != null) {
                                try {
                                    price += slab1.toInt()
                                } catch (e: Exception) {
                                    continue
                                }
                            }
                            isAdded = true
                        }
                    }
                }
            }
        }

        if (isAdded) {
            summaryMap[baseModule.code] = ModuleGroupSummary(baseModule.code, baseModule.name, price)
        } else {
            summaryMap.remove(baseModule.code)
        }

        var moduleCost = 0
        val moduleSummaryList: ArrayList<ModuleGroupSummary> = ArrayList()
        for (key in summaryMap.keys) {
            val item = summaryMap[key] ?: continue
            moduleSummaryList.add(item)
            moduleCost += item.price
        }

        calculateSummaryCost(moduleCost)
        bindSummaryCostDataToUI()
        moduleSummaryAdapter.submitList(moduleSummaryList)
    }

    private fun changeHeader(code: String) {
        binding.appBarMain.contentMain.headerStart.root.visibility = if (code == "START") View.VISIBLE else View.GONE
        binding.appBarMain.contentMain.headerFMS.root.visibility = if (code == "FMS") View.VISIBLE else View.GONE
        binding.appBarMain.contentMain.headerHCM.root.visibility = if (code == "HCM") View.VISIBLE else View.GONE
        binding.appBarMain.contentMain.headerPPC.root.visibility = if (code == "PPC") View.VISIBLE else View.GONE
        binding.appBarMain.contentMain.headerEAM.root.visibility = if (code == "EAM") View.VISIBLE else View.GONE
        binding.appBarMain.contentMain.headerCSC.root.visibility = if (code == "CSC") View.VISIBLE else View.GONE
        binding.appBarMain.contentMain.headerPIP.root.visibility = if (code == "PIP") View.VISIBLE else View.GONE

        when(code) {
            "START" -> {
                binding.appBarMain.contentMain.linearHeader.visibility = View.VISIBLE
            }
            "FMS" -> {
                binding.appBarMain.contentMain.linearHeader.visibility = View.VISIBLE
            }
            "SDM" -> {
                binding.appBarMain.contentMain.linearHeader.visibility = View.GONE
            }
            "CRM" -> {
                binding.appBarMain.contentMain.linearHeader.visibility = View.GONE
            }
            "SCM" -> {
                binding.appBarMain.contentMain.linearHeader.visibility = View.GONE
            }
            "HCM" -> {
                binding.appBarMain.contentMain.linearHeader.visibility = View.VISIBLE
            }
            "PPC" -> {
                binding.appBarMain.contentMain.linearHeader.visibility = View.VISIBLE
            }
            "EAM" -> {
                binding.appBarMain.contentMain.linearHeader.visibility = View.VISIBLE
            }
            "CSC" -> {
                binding.appBarMain.contentMain.linearHeader.visibility = View.VISIBLE
            }
            "PIP" -> {
                binding.appBarMain.contentMain.linearHeader.visibility = View.VISIBLE
            }
        }
    }

    private fun calculateSummaryCost(moduleCost: Int) {
        costSoftwareLicense = moduleCost + costAdditionalUsers

        costImplementation = costRequirementAnalysis + costDeployment + costConfiguration + costOnsiteAdoptionSupport + costTraining + costProjectManagement

        costSoftwareCustomizationTotal = costSoftwareCustomization + costCustomizedReport

        costConsultancyServices = costConsultancy

        costAnnualMaintenanceTotal = costAnnualMaintenance

        costTotal = costSoftwareLicense + costImplementation + costSoftwareCustomizationTotal + costConsultancyServices + costAnnualMaintenanceTotal
    }

    private fun bindSummaryCostDataToUI() {
        val costSoftwareLicenseText = if (costSoftwareLicense > 0) "৳$costSoftwareLicense" else "-"
        binding.appBarMain.contentMain.summarySheet.costSoftwareLicense.text = costSoftwareLicenseText

        val costAdditionalUsersText = if (costAdditionalUsers > 0) "৳$costAdditionalUsers" else "-"
        binding.appBarMain.contentMain.summarySheet.costAdditionalUsers.text = costAdditionalUsersText

        val costImplementationText = if (costImplementation > 0) "৳$costImplementation" else "-"
        binding.appBarMain.contentMain.summarySheet.costImplementation.text = costImplementationText

        val costRequirementAnalysisText = if (costRequirementAnalysis > 0) "৳$costRequirementAnalysis" else "-"
        binding.appBarMain.contentMain.summarySheet.costRequirementAnalysis.text = costRequirementAnalysisText

        val costDeploymentText = if (costDeployment > 0) "৳$costDeployment" else "-"
        binding.appBarMain.contentMain.summarySheet.costDeployment.text = costDeploymentText

        val costConfigurationText = if (costConfiguration > 0) "৳$costConfiguration" else "-"
        binding.appBarMain.contentMain.summarySheet.costConfiguration.text = costConfigurationText

        val costOnsiteAdoptionSupportText = if (costOnsiteAdoptionSupport > 0) "৳$costOnsiteAdoptionSupport" else "-"
        binding.appBarMain.contentMain.summarySheet.costOnsiteAdoptionSupport.text = costOnsiteAdoptionSupportText

        val costTrainingText = if (costTraining > 0) "৳$costTraining" else "-"
        binding.appBarMain.contentMain.summarySheet.costTraining.text = costTrainingText

        val costProjectManagementText = if (costProjectManagement > 0) "৳$costProjectManagement" else "-"
        binding.appBarMain.contentMain.summarySheet.costProjectManagement.text = costProjectManagementText

        val costSoftwareCustomizationTotalText = if (costSoftwareCustomizationTotal > 0) "৳$costSoftwareCustomizationTotal" else "-"
        binding.appBarMain.contentMain.summarySheet.costSoftwareCustomizationTotal.text = costSoftwareCustomizationTotalText

        val costSoftwareCustomizationText = if (costSoftwareCustomization > 0) "৳$costSoftwareCustomization" else "-"
        binding.appBarMain.contentMain.summarySheet.costSoftwareCustomization.text = costSoftwareCustomizationText

        val costCustomizedReportText = if (costCustomizedReport > 0) "৳$costCustomizedReport" else "-"
        binding.appBarMain.contentMain.summarySheet.costCustomizedReport.text = costCustomizedReportText

        val costConsultancyServicesText = if (costConsultancyServices > 0) "৳$costConsultancyServices" else "-"
        binding.appBarMain.contentMain.summarySheet.costConsultancyServices.text = costConsultancyServicesText

        val costConsultancyText = if (costConsultancy > 0) "৳$costConsultancy" else "-"
        binding.appBarMain.contentMain.summarySheet.costConsultancy.text = costConsultancyText

        val costAnnualMaintenanceTotalText = if (costAnnualMaintenanceTotal > 0) "৳$costAnnualMaintenanceTotal" else "-"
        binding.appBarMain.contentMain.summarySheet.costAnnualMaintenanceTotal.text = costAnnualMaintenanceTotalText

        val costAnnualMaintenanceText = if (costAnnualMaintenance > 0) "৳$costAnnualMaintenance" else "-"
        binding.appBarMain.contentMain.summarySheet.costAnnualMaintenance.text = costAnnualMaintenanceText

        val costTotalText = if (costTotal > 0) "৳$costTotal" else "-"
        binding.appBarMain.contentMain.summarySheet.costTotal.text = costTotalText
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            binding.drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.ham_burger_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.moduleList -> {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.END)
                } else {
                    binding.drawerLayout.openDrawer(GravityCompat.END)
                }
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