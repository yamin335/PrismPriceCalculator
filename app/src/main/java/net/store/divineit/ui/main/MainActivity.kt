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
import net.store.divineit.models.*
import net.store.divineit.ui.BaseServiceModuleListAdapter
import net.store.divineit.ui.ModuleGroupAdapter
import net.store.divineit.ui.ModuleGroupSummaryListAdapter
import net.store.divineit.ui.base.BaseActivity
import net.store.divineit.utils.AppConstants
import net.store.divineit.ui.login.LoginActivity
import java.io.*
import kotlin.math.abs
import kotlin.math.ceil

@AndroidEntryPoint
class MainActivity : BaseActivity<MainActivityBinding, MainActivityViewModel>() {
    companion object {
        var productId: String = ""
    }
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
    private lateinit var multiplierListAdapter: MultiplierListAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpToolbar()

        calculateSummaryCost(0)
        bindSummaryCostDataToUI()

        mDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent?): Boolean {
                if (summarySheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    summarySheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                } else {
                    summarySheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
                return true
            }
        })

        moduleSummaryAdapter = ModuleGroupSummaryListAdapter()
        binding.appBarMain.contentMain.summarySheet.recyclerSummary.adapter = moduleSummaryAdapter

        multiplierListAdapter = MultiplierListAdapter( callback = { index, multiplierCode, position ->
            viewModel.baseModuleList[selectedBaseModulePosition].multipliers[position].slabIndex = index
            calculateModuleAndFeaturePrice(Pair(index, multiplierCode))
            calculateSummary()
            moduleGroupAdapter.notifyDataSetChanged()
        }, sliderCallback = { code, value ->
            when (code) {
                "custom" -> {
                    viewModel.costSoftwareCustomization = value * AppConstants.unitPriceSoftwareCustomization
                    calculateSummary()
                }

                "report" -> {
                    viewModel.costCustomizedReport = value * AppConstants.unitPriceCustomizedReports
                    calculateSummary()
                }
            }
        })

        binding.appBarMain.contentMain.multipliers.apply {
            setHasFixedSize(true)
            adapter = multiplierListAdapter
        }

        viewModel.baseModuleListTemp.observe(this) { baseModuleList ->
            baseModuleList?.let {
                viewModel.baseModuleList = baseModuleList as ArrayList<BaseServiceModule>
                baseServiceAdapter = BaseServiceModuleListAdapter(viewModel.baseModuleList) { baseModule, position ->
                    selectedBaseModulePosition = position
                    loadHeaderMultipliers(viewModel.baseModuleList[selectedBaseModulePosition])
                    binding.drawerLayout.closeDrawer(GravityCompat.END)
                    CoroutineScope(Dispatchers.Main.immediate).launch {
                        delay(250)
                        moduleGroupAdapter = ModuleGroupAdapter(viewModel.baseModuleList[selectedBaseModulePosition].code ?: "", viewModel.baseModuleList[selectedBaseModulePosition].moduleGroups ?: ArrayList()) {
                            calculateSummary()
                        }
                        binding.appBarMain.contentMain.recyclerView.apply {
                            adapter = moduleGroupAdapter
                        }
                    }
                }
                binding.baseServiceModuleRecycler.adapter = baseServiceAdapter
                if (it.isEmpty()) return@observe
                loadHeaderMultipliers(it[0])

                moduleGroupAdapter = ModuleGroupAdapter(viewModel.baseModuleList[0].code ?: "", viewModel.baseModuleList[0].moduleGroups ?: ArrayList()) {
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
                calculateSummary()
            }
        }

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
            if (summarySheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                summarySheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            if (preferencesHelper.isLoggedIn) {
                viewModel.submitSummary()
            } else {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            }
        }

        viewModel.productDetails(productId)

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

//        val inputStream: InputStream = resources.openRawResource(R.raw.divine)
//        val writer: Writer = StringWriter()
//        val buffer = CharArray(1024)
//        inputStream.use { stream ->
//            val reader: Reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
//            var n: Int
//            while (reader.read(buffer).also { n = it } != -1) {
//                writer.write(buffer, 0, n)
//            }
//        }
//
//        val jsonString: String = writer.toString()
//        baseModuleList = Gson().fromJson(jsonString, Array<BaseServiceModule>::class.java).toList()
    }

    private fun setUpToolbar() {
        binding.appBarMain.toolbar.title = "Prism Price Calculator"
        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun calculateSummary() {
        val baseModule = viewModel.baseModuleList[selectedBaseModulePosition]
        var isAdded = false
        var price = 0

        val summaryModuleFeatureList: ArrayList<SummaryModuleFeature> = ArrayList()
        var summaryModuleTotalPrice = 0

        for (moduleGroup in baseModule.moduleGroups) {

            if (moduleGroup.modules.isNullOrEmpty()) continue

            for (module in moduleGroup.modules) {
                if (module.isAdded) {
                    var slabPrice = 0
                    if (module.defaultprice == 0.0) {
                        if (module.price.isEmpty()) continue
                        val modulePrice = module.price[0]
                        if (modulePrice.isBlank()) continue
                        try {
                            slabPrice = modulePrice.toDouble().toInt()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        slabPrice = module.defaultprice.toInt()
                    }

                    price += slabPrice
                    summaryModuleTotalPrice += slabPrice
                    summaryModuleFeatureList.add(SummaryModuleFeature(name = module.name, code = module.code, multiplier = module.multiplier,
                        multipliercode = "", price = module.price, type = "module", defaultprice = module.defaultprice.toInt(),
                        totalamount = module.defaultprice.toInt()))
                    isAdded = true
                }

                if (module.features.isNullOrEmpty()) continue

                for (feature in module.features) {
                    if (feature.isAdded) {
                        var slabPrice = 0
                        if (feature.defaultprice == 0.0) {
                            if (feature.price.isEmpty()) continue
                            val featurePrice = feature.price[0]
                            if (featurePrice.isBlank()) continue
                            try {
                                slabPrice = featurePrice.toDouble().toInt()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            slabPrice = feature.defaultprice.toInt()
                        }

                        price += slabPrice
                        summaryModuleTotalPrice += slabPrice
                        summaryModuleFeatureList.add(SummaryModuleFeature(name = feature.name, code = feature.code, multiplier = feature.multiplier,
                            multipliercode = "", price = feature.price, type = "feature", defaultprice = feature.defaultprice.toInt(), totalamount = feature.defaultprice.toInt()))
                        isAdded = true
                    }
                }
            }
        }

        val licensingParameters: ArrayList<LicensingParameter> = ArrayList()

        for (multiplier in baseModule.multipliers) {
            val slabIndex = multiplier.slabIndex
            if (multiplier.slabs.isNullOrEmpty()) continue
            val slab = multiplier.slabs[slabIndex]
            val isNumber = slab.matches("((\\d+\\.?)*\\d*)".toRegex())
            val param: LicensingParameter = if (isNumber) {
                if (multiplier.slabConfig?.showRange == true) {
                    var prefix = ""
                    if (multiplier.slabTexts.size > slabIndex) {
                        prefix = multiplier.slabTexts[slabIndex]
                    }

                    val increment = 1
                    var startItem = increment

                    if (slabIndex > 0) {
                        try {
                            val previousItem = multiplier.slabs[slabIndex - 1].toDouble().toInt()
                            startItem = previousItem + increment
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    try {
                        val slabPrice = slab.toDouble().toInt()
                        val slabText = if (prefix.isBlank()) {
                            "$startItem-${slabPrice}"
                        } else {
                            "$prefix($startItem-${slabPrice})"
                        }
                        LicensingParameter(multiplier.code, slabText, 0)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        LicensingParameter(multiplier.code, slab, 0)
                    }
                } else {
                    var prefix = ""
                    if (multiplier.slabTexts.size > slabIndex) {
                        prefix = multiplier.slabTexts[slabIndex]
                    }

                    try {
                        val slabPrice = slab.toDouble().toInt()
                        val slabText = if (prefix.isBlank()) {
                            "$slabPrice"
                        } else {
                            "$prefix(${slabPrice})"
                        }
                        LicensingParameter(multiplier.code, slabText, 0)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        LicensingParameter(multiplier.code, slab, 0)
                    }
                }
            } else {
                LicensingParameter(multiplier.code, slab, 0)
            }
            licensingParameters.add(param)
        }

        if (selectedBaseModulePosition == 0) {
            viewModel.softwareLicenseModuleMap[baseModule.code ?: ""] = SoftwareLicenseModule(name = baseModule. name,
                totalamount = summaryModuleTotalPrice, code = baseModule.code,
                licensingparameters = licensingParameters, features = summaryModuleFeatureList)
        } else {
            if (isAdded) {
                viewModel.summaryMap[baseModule.code ?: ""] = ModuleGroupSummary(baseModule.code ?: "", baseModule.name ?: "", price)
                viewModel.softwareLicenseModuleMap[baseModule.code ?: ""] = SoftwareLicenseModule(name = baseModule. name,
                    totalamount = summaryModuleTotalPrice, code = baseModule.code,
                    licensingparameters = licensingParameters, features = summaryModuleFeatureList)
            } else {
                viewModel.summaryMap.remove(key = baseModule.code)
                viewModel.softwareLicenseModuleMap.remove(key = baseModule.code)
            }
        }

        var moduleCost = 0
        val moduleSummaryList: ArrayList<ModuleGroupSummary> = ArrayList()
        for (key in viewModel.summaryMap.keys) {
            val item = viewModel.summaryMap[key] ?: continue
            moduleSummaryList.add(item)
            moduleCost += item.price
        }

        calculateSummaryCost(moduleCost)
        bindSummaryCostDataToUI()
        moduleSummaryAdapter.submitList(moduleSummaryList)

        binding.appBarMain.contentMain.summarySheet.btnSubmit.isEnabled = viewModel.softwareLicenseModuleMap.isNotEmpty()
    }

    private fun loadHeaderMultipliers(baseModule: BaseServiceModule) {
        val validMultipliers = baseModule.multipliers.filter {
            return@filter (it.label?.isNotBlank() == true) && it.slabConfig?.hideInApp != true
        }
        baseModule.multipliers = validMultipliers
        binding.appBarMain.contentMain.linearHeader.visibility = if (baseModule.multipliers.isEmpty()) View.GONE else View.VISIBLE
        multiplierListAdapter.submitList(baseModule.multipliers)
    }

    private fun calculateSummaryCost(moduleCost: Int) {
        val totalUsers = ceil((moduleCost.toDouble() / AppConstants.perUserCost)).toInt()
        viewModel.additionalUsers = if (totalUsers <= AppConstants.additionalUsers) AppConstants.additionalUsers - totalUsers else 0 //abs((AppConstants.additionalUsers - totalUsers))
        viewModel.usersIncluded = totalUsers //abs((AppConstants.additionalUsers - totalUsers)) //if (totalUsers >= AppConstants.additionalUsers) totalUsers - AppConstants.additionalUsers else totalUsers
        viewModel.costAdditionalUsers = (AppConstants.costAdditionalUsers / AppConstants.additionalUsers) * viewModel.additionalUsers
        viewModel.costSoftwareLicense = moduleCost + viewModel.costAdditionalUsers

        viewModel.costRequirementAnalysis = (viewModel.costSoftwareLicense * AppConstants.percentRequirementAnalysis) / 100
        viewModel.costDeployment = (viewModel.costSoftwareLicense * AppConstants.percentDeployment) / 100
        viewModel.costOnsiteAdoptionSupport = (viewModel.costSoftwareLicense * AppConstants.percentOnSiteAdoption) / 100
        viewModel.costTraining = (viewModel.costSoftwareLicense * AppConstants.percentTraining) / 100
        viewModel.costProjectManagement = (viewModel.costSoftwareLicense * AppConstants.percentProjectManagement) / 100

        viewModel.costImplementation = viewModel.costRequirementAnalysis +
                viewModel.costDeployment + viewModel.costConfiguration +
                viewModel.costOnsiteAdoptionSupport +
                viewModel.costTraining + viewModel.costProjectManagement

        viewModel.costSoftwareCustomizationTotal = viewModel.costSoftwareCustomization + viewModel.costCustomizedReport

        viewModel.costConsultancy = (viewModel.costSoftwareLicense * AppConstants.percentConsultancy) / 100

        viewModel.costConsultancyServices = viewModel.costConsultancy

        viewModel.costAnnualMaintenance = (viewModel.costSoftwareLicense * AppConstants.percentMaintenance) / 100

        viewModel.costAnnualMaintenanceTotal = viewModel.costAnnualMaintenance

        viewModel.costTotal = viewModel.costSoftwareLicense + viewModel.costImplementation + viewModel.costSoftwareCustomizationTotal + viewModel.costConsultancyServices + viewModel.costAnnualMaintenanceTotal
    }

    private fun bindSummaryCostDataToUI() {
        val costSoftwareLicenseText = if (viewModel.costSoftwareLicense > 0) "৳${viewModel.costSoftwareLicense}" else "-"
        binding.appBarMain.contentMain.summarySheet.costSoftwareLicense.text = costSoftwareLicenseText

        val usersIncludedText = "${viewModel.usersIncluded} Users Included"
        binding.appBarMain.contentMain.summarySheet.usersIncluded.text = usersIncludedText

        if (viewModel.additionalUsers > 0) {
            val additionalUsersText = "${viewModel.additionalUsers} Additional User"
            binding.appBarMain.contentMain.summarySheet.additionalUsers.text = additionalUsersText
            val costAdditionalUsersText = if (viewModel.costAdditionalUsers > 0) "৳${viewModel.costAdditionalUsers}" else "-"
            binding.appBarMain.contentMain.summarySheet.costAdditionalUsers.text = costAdditionalUsersText
            binding.appBarMain.contentMain.summarySheet.linearAdditionalUsers.visibility = View.VISIBLE
        } else {
            binding.appBarMain.contentMain.summarySheet.linearAdditionalUsers.visibility = View.GONE
        }

        val costImplementationText = if (viewModel.costImplementation > 0) "৳${viewModel.costImplementation}" else "-"
        binding.appBarMain.contentMain.summarySheet.costImplementation.text = costImplementationText

        val costRequirementAnalysisText = if (viewModel.costRequirementAnalysis > 0) "৳${viewModel.costRequirementAnalysis}" else "-"
        binding.appBarMain.contentMain.summarySheet.costRequirementAnalysis.text = costRequirementAnalysisText

        val costDeploymentText = if (viewModel.costDeployment > 0) "৳${viewModel.costDeployment}" else "-"
        binding.appBarMain.contentMain.summarySheet.costDeployment.text = costDeploymentText

        val costConfigurationText = if (viewModel.costConfiguration > 0) "৳${viewModel.costConfiguration}" else "-"
        binding.appBarMain.contentMain.summarySheet.costConfiguration.text = costConfigurationText

        val costOnsiteAdoptionSupportText = if (viewModel.costOnsiteAdoptionSupport > 0) "৳${viewModel.costOnsiteAdoptionSupport}" else "-"
        binding.appBarMain.contentMain.summarySheet.costOnsiteAdoptionSupport.text = costOnsiteAdoptionSupportText

        val costTrainingText = if (viewModel.costTraining > 0) "৳${viewModel.costTraining}" else "-"
        binding.appBarMain.contentMain.summarySheet.costTraining.text = costTrainingText

        val costProjectManagementText = if (viewModel.costProjectManagement > 0) "৳${viewModel.costProjectManagement}" else "-"
        binding.appBarMain.contentMain.summarySheet.costProjectManagement.text = costProjectManagementText

        val costSoftwareCustomizationTotalText = if (viewModel.costSoftwareCustomizationTotal > 0) "৳${viewModel.costSoftwareCustomizationTotal}" else "-"
        binding.appBarMain.contentMain.summarySheet.costSoftwareCustomizationTotal.text = costSoftwareCustomizationTotalText

        val costSoftwareCustomizationText = if (viewModel.costSoftwareCustomization > 0) "৳${viewModel.costSoftwareCustomization}" else "-"
        binding.appBarMain.contentMain.summarySheet.costSoftwareCustomization.text = costSoftwareCustomizationText

        val costCustomizedReportText = if (viewModel.costCustomizedReport > 0) "৳${viewModel.costCustomizedReport}" else "-"
        binding.appBarMain.contentMain.summarySheet.costCustomizedReport.text = costCustomizedReportText

        val costConsultancyServicesText = if (viewModel.costConsultancyServices > 0) "৳${viewModel.costConsultancyServices}" else "-"
        binding.appBarMain.contentMain.summarySheet.costConsultancyServices.text = costConsultancyServicesText

        val costConsultancyText = if (viewModel.costConsultancy > 0) "৳${viewModel.costConsultancy}" else "-"
        binding.appBarMain.contentMain.summarySheet.costConsultancy.text = costConsultancyText

        val costAnnualMaintenanceTotalText = if (viewModel.costAnnualMaintenanceTotal > 0) "৳${viewModel.costAnnualMaintenanceTotal}" else "-"
        binding.appBarMain.contentMain.summarySheet.costAnnualMaintenanceTotal.text = costAnnualMaintenanceTotalText

        val costAnnualMaintenanceText = if (viewModel.costAnnualMaintenance > 0) "৳${viewModel.costAnnualMaintenance}" else "-"
        binding.appBarMain.contentMain.summarySheet.costAnnualMaintenance.text = costAnnualMaintenanceText

        val costTotalText = if (viewModel.costTotal > 0) "৳${viewModel.costTotal}" else "-"
        binding.appBarMain.contentMain.summarySheet.costTotal.text = costTotalText
    }

    private fun calculateModuleAndFeaturePrice(pair: Pair<Int, String>) {
        if (viewModel.baseModuleList[selectedBaseModulePosition].moduleGroups.isNullOrEmpty()) return

        for (groupIndex in viewModel.baseModuleList[selectedBaseModulePosition].moduleGroups.indices) {

            if (viewModel.baseModuleList[selectedBaseModulePosition].moduleGroups[groupIndex].modules.isNullOrEmpty()) return

            for (moduleIndex in viewModel.baseModuleList[selectedBaseModulePosition].moduleGroups[groupIndex].modules.indices) {
                val moduleMultiplierCode = viewModel.baseModuleList[selectedBaseModulePosition].moduleGroups[groupIndex].modules[moduleIndex].multiplier
                if (moduleMultiplierCode is String && moduleMultiplierCode == pair.second) {
                    calculateModulePrice(viewModel.baseModuleList[selectedBaseModulePosition].moduleGroups[groupIndex].modules[moduleIndex], pair.first)
                }

                if (viewModel.baseModuleList[selectedBaseModulePosition].moduleGroups[groupIndex].modules[moduleIndex].features.isNullOrEmpty()) return

                for (featureIndex in viewModel.baseModuleList[selectedBaseModulePosition].moduleGroups[groupIndex].modules[moduleIndex].features.indices) {
                    val featureMultiplierCode = viewModel.baseModuleList[selectedBaseModulePosition].moduleGroups[groupIndex].modules[moduleIndex].features[featureIndex].multiplier
                    if (featureMultiplierCode is String && moduleMultiplierCode == pair.second) {
                        calculateFeaturePrice(viewModel.baseModuleList[selectedBaseModulePosition].moduleGroups[groupIndex].modules[moduleIndex].features[featureIndex], pair.first)
                    }
                }
            }
        }
    }

    private fun calculateModulePrice(module:  ServiceModule, index: Int) {
        if (module.price.size <= index) return
        val price = module.price[index]
        if (price.isBlank()) return
        try {
            module.defaultprice = price.toDouble()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun calculateFeaturePrice(feature:  Feature, index: Int) {
        if (feature.price.size <= index) return
        val price = feature.price[index]
        if (price.isBlank()) return
        try {
            feature.defaultprice = price.toDouble()
        } catch (e: Exception) {
            e.printStackTrace()
        }
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