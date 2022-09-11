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
import net.store.divineit.api.ApiCallStatus
import net.store.divineit.databinding.MainActivityBinding
import net.store.divineit.models.*
import net.store.divineit.ui.BaseServiceModuleListAdapter
import net.store.divineit.ui.ModuleGroupAdapter
import net.store.divineit.ui.ModuleGroupSummaryListAdapter
import net.store.divineit.ui.base.BaseActivity
import net.store.divineit.ui.home.HomeActivity
import net.store.divineit.utils.AppConstants
import net.store.divineit.ui.login.LoginActivity
import net.store.divineit.utils.AppGlobalValues
import net.store.divineit.utils.hideKeyboard
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
    private var isQuotationDetails = false

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

        multiplierListAdapter = MultiplierListAdapter( callback = { index, multiplierCode, position, customValue, baseModuleCode ->
            hideKeyboard()
            if (baseModuleCode == null || viewModel.baseModuleList[selectedBaseModulePosition].code == null ||
                baseModuleCode != viewModel.baseModuleList[selectedBaseModulePosition].code) return@MultiplierListAdapter

            viewModel.baseModuleList[selectedBaseModulePosition].multipliers[position].slabIndex = index
            viewModel.baseModuleList[selectedBaseModulePosition].multipliers[position].customValue = customValue

            if (viewModel.baseModuleList[selectedBaseModulePosition].multipliers[position].slabs.size == index) {
                calculateModuleAndFeaturePrice(-1, multiplierCode, customValue)
            } else {
                calculateModuleAndFeaturePrice(index, multiplierCode, customValue)
                calculateSummary()
                if (viewModel.responsibleMultipliersOfBaseModules[baseModuleCode] != null) {
                    val map = viewModel.responsibleMultipliersOfBaseModules[baseModuleCode] ?: return@MultiplierListAdapter
                    if (map[multiplierCode] == true) {
                        viewModel.apiCallStatus.postValue(ApiCallStatus.LOADING)
                        CoroutineScope(Dispatchers.Main.immediate).launch {
                            delay(1500)
                            viewModel.apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        }
                        CoroutineScope(Dispatchers.Main.immediate).launch {
                            delay(100)
                            moduleGroupAdapter.modulesAlreadyLoadedForPosition = HashMap()
                            moduleGroupAdapter.itemNotified = true
                            moduleGroupAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }, sliderCallback = { code, value, baseModuleCode ->
            if (baseModuleCode == null || viewModel.baseModuleList[selectedBaseModulePosition].code == null ||
                baseModuleCode != viewModel.baseModuleList[selectedBaseModulePosition].code) return@MultiplierListAdapter

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
        }, viewModel.costSoftwareCustomization, viewModel.costCustomizedReport)

        binding.appBarMain.contentMain.multipliers.apply {
            setHasFixedSize(true)
            adapter = multiplierListAdapter
        }

        viewModel.baseModuleListTemp.observe(this) { baseModuleList ->
            baseModuleList?.let {
                prepareBaseModuleListUI(it, null)
            }
            viewModel.baseModuleListTemp.postValue(null)
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
                if (isQuotationDetails) {
                    if (viewModel.moduleChangeMapOld.isNotEmpty()
                        && viewModel.moduleChangeMapNew.isNotEmpty()
                        && viewModel.moduleChangeMapOld.size != viewModel.moduleChangeMapNew.size) {
                        // Update summary
                        viewModel.submitOrUpdateSummary(viewModel.quotationDetails)
                    } else {
                        var hasChanged = false
                        for (key in viewModel.moduleChangeMapOld.keys) {
                            if (viewModel.moduleChangeMapOld[key] != viewModel.moduleChangeMapNew[key]) {
                                hasChanged = true
                                break
                            }
                        }

                        if (hasChanged) {
                            // Update summary
                            viewModel.submitOrUpdateSummary(viewModel.quotationDetails)
                        } else {
                            viewModel.toastWarning.postValue("No changes found!")
                        }
                    }
                } else {
                    viewModel.submitOrUpdateSummary(null)
                }
            } else {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            }
        }

        viewModel.quotationSubmitResponse.observe(this) {
            goHome()
        }

        viewModel.quotationDetailsResponse.observe(this) { response ->
            response?.let {
                viewModel.quotationDetails = it.first
                prepareBaseModuleListUI(it.second ?: ArrayList(), it.first)
            }
            viewModel.quotationDetailsResponse.postValue(null)
        }

        val bundle = intent.extras
        if (bundle == null) {
            viewModel.productDetails(productId)
        } else {
            val quotationId = bundle.getString(AppConstants.KEY_QUOTATION_ID, "")
            val productId = bundle.getString(AppConstants.KEY_PRODUCT_ID, "")
            isQuotationDetails = true
            viewModel.quotationDetails(productId, quotationId)
        }
    }

    private fun prepareBaseModuleListUI(baseModuleList: List<BaseServiceModule>, quotation: SummaryResponseQuotation?) {
        val quotationModuleMap: HashMap<String, SummaryResponseFeature> = HashMap()
        val multiplierMap: HashMap<String?, Pair<String?, Int?>> = HashMap()

        if (quotation != null) {
            quotation.Customization?.modules?.let {
                for (item in it) {
                    when (item.name) {
                        "Software Customization" -> {
                            viewModel.costSoftwareCustomization = (item.details_value ?: 0) * (item.details_multiplier ?: 0)
                        }

                        "Customized Report" -> {
                            viewModel.costCustomizedReport = (item.details_value ?: 0) * (item.details_multiplier ?: 0)
                        }
                    }
                }
            }

            val softwareLicenseModules = quotation.Software_License?.modules ?: ArrayList()
            for (module in softwareLicenseModules) {
                viewModel.moduleChangeMapOld[module.code] = module.totalamount
                viewModel.moduleChangeMapNew[module.code] = module.totalamount
                val parameters = module.licensingparameters ?: ArrayList()
                for (param in parameters) {
                    multiplierMap["${param.name}${module.code}"] = Pair(param.value, null)
                }
                if (module.features.isNullOrEmpty()) continue
                for (feature in module.features) {
                    quotationModuleMap[feature.code ?: ""] = feature
                }

                viewModel.summaryMap[module.code ?: ""] = ModuleGroupSummary(module.code ?: "", module.name ?: "",
                    module.totalamount ?: 0)
                viewModel.softwareLicenseModuleMap[module.code ?: ""] = module
            }

            if (quotationModuleMap.isNotEmpty()) {
                for (baseIndex in viewModel.baseModuleList.indices) {
                    for (multiplierIndex in viewModel.baseModuleList[baseIndex].multipliers.indices) {
                        val multiplier = viewModel.baseModuleList[baseIndex].multipliers[multiplierIndex]
                        if (multiplier.slabLabels.isNullOrEmpty()) continue
                        var isCustomValue = false
                        val keyCode = "${multiplier.code}${viewModel.baseModuleList[baseIndex].code}"
                        val pair = multiplierMap[keyCode]
                        if (pair != null) {
                            slabLoop@ for (slabIndex in multiplier.slabLabels.indices) {
                                val slabLabel = viewModel.baseModuleList[baseIndex].multipliers[multiplierIndex].slabLabels[slabIndex]
                                if (pair.first == slabLabel) {
                                    viewModel.baseModuleList[baseIndex].multipliers[multiplierIndex].slabIndex = slabIndex
                                    multiplierMap[keyCode] = Pair(pair.first, slabIndex)
                                    isCustomValue = false
                                    break@slabLoop
                                } else {
                                    // Custom value
                                    isCustomValue = true
                                }
                            }
                            if (isCustomValue) {
                                viewModel.baseModuleList[baseIndex].multipliers[multiplierIndex].customValue = pair.first
                                viewModel.baseModuleList[baseIndex].multipliers[multiplierIndex].slabIndex = viewModel.baseModuleList[baseIndex].multipliers[multiplierIndex].slabLabels.size
                            }
                        }
                    }
                    if (viewModel.baseModuleList[baseIndex].moduleGroups.isNullOrEmpty()) continue

                    for (groupIndex in viewModel.baseModuleList[baseIndex].moduleGroups.indices) {

                        if (viewModel.baseModuleList[baseIndex].moduleGroups[groupIndex].modules.isNullOrEmpty()) continue

                        for (moduleIndex in viewModel.baseModuleList[baseIndex].moduleGroups[groupIndex].modules.indices) {
                            val moduleMultiplierCode = viewModel.baseModuleList[baseIndex].moduleGroups[groupIndex].modules[moduleIndex].multiplier
                            val keyModule = "$moduleMultiplierCode${viewModel.baseModuleList[baseIndex].code}"
                            val pair = multiplierMap[keyModule]
                            if (pair != null) {
                                val slabIndex = pair.second
                                if (slabIndex != null) {
                                    calculateModulePrice(baseIndex, groupIndex, moduleIndex, slabIndex, "")
                                }
                            }
                            val moduleCode = viewModel.baseModuleList[baseIndex].moduleGroups[groupIndex].modules[moduleIndex].code
                            val summaryModule = quotationModuleMap[moduleCode]
                            if (summaryModule != null) {
                                viewModel.baseModuleList[baseIndex].moduleGroups[groupIndex].modules[moduleIndex].isAdded = true
                            }

                            if (viewModel.baseModuleList[baseIndex].moduleGroups[groupIndex].modules[moduleIndex].features.isNullOrEmpty()) continue

                            for (featureIndex in viewModel.baseModuleList[baseIndex].moduleGroups[groupIndex].modules[moduleIndex].features.indices) {
                                val featureMultiplierCode = viewModel.baseModuleList[baseIndex].moduleGroups[groupIndex].modules[moduleIndex].features[featureIndex].multiplier
                                val keyFeature = "$featureMultiplierCode${viewModel.baseModuleList[baseIndex].code}"
                                val pair2 = multiplierMap[keyFeature]
                                if (pair2 != null) {
                                    val slabIndex = pair2.second
                                    if (slabIndex != null) {
                                        calculateFeaturePrice(baseIndex, groupIndex, moduleIndex, featureIndex, slabIndex, "")
                                    }
                                }
                                val featureCode = viewModel.baseModuleList[baseIndex].moduleGroups[groupIndex].modules[moduleIndex].features[featureIndex].code
                                val summaryFeature = quotationModuleMap[featureCode]
                                if (summaryFeature != null) {
                                    viewModel.baseModuleList[baseIndex].moduleGroups[groupIndex].modules[moduleIndex].features[featureIndex].isAdded = true
                                }
                            }
                        }
                    }
                }
            }
        }

        baseServiceAdapter = BaseServiceModuleListAdapter(viewModel.baseModuleList) { baseModule, position ->
            hideKeyboard()
            selectedBaseModulePosition = position
            loadHeaderMultipliers(viewModel.baseModuleList[selectedBaseModulePosition])
            binding.drawerLayout.closeDrawer(GravityCompat.END)
//                    viewModel.apiCallStatus.postValue(ApiCallStatus.LOADING)
//                    CoroutineScope(Dispatchers.Main.immediate).launch {
//                        delay(1500)
//                        viewModel.apiCallStatus.postValue(ApiCallStatus.SUCCESS)
//                    }
            CoroutineScope(Dispatchers.Main.immediate).launch {
                //delay(250)
                moduleGroupAdapter = ModuleGroupAdapter(viewModel.baseModuleList[selectedBaseModulePosition].code ?: "",
                    viewModel.baseModuleList[selectedBaseModulePosition].moduleGroups
                ) {
                    calculateSummary()
                }

                val innerLLM = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
                innerLLM.initialPrefetchItemCount = 1

                binding.appBarMain.contentMain.recyclerView.apply {
                    isNestedScrollingEnabled = false
                    layoutManager = innerLLM
                    adapter = moduleGroupAdapter
                }
            }
        }
        binding.baseServiceModuleRecycler.adapter = baseServiceAdapter
        if (baseModuleList.isEmpty()) return
        loadHeaderMultipliers(baseModuleList[0])

        moduleGroupAdapter = ModuleGroupAdapter(viewModel.baseModuleList[0].code ?: "",
            viewModel.baseModuleList[0].moduleGroups
        ) {
            calculateSummary()
        }

        val innerLLM = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        innerLLM.initialPrefetchItemCount = 1

        binding.appBarMain.contentMain.recyclerView.apply {
            isNestedScrollingEnabled = false
            //setHasFixedSize(true)
            layoutManager = innerLLM
            adapter = moduleGroupAdapter
        }
        calculateSummary()
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

        val summaryModuleFeatureList: ArrayList<SummaryResponseFeature> = ArrayList()
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
                    summaryModuleFeatureList.add(SummaryResponseFeature(name = module.name, code = module.code, parentcode = "",
                        description = module.description, multiplier = module.multiplier, multipliercode = "", excludeInAll = false,
                        type = "module", discount = 0, totalamount = module.defaultprice.toInt(), price = module.price, defaultprice = module.defaultprice))
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
                        summaryModuleFeatureList.add(SummaryResponseFeature(name = feature.name, code = feature.code, parentcode = feature.parentcode,
                        description = feature.description, multiplier = feature.multiplier, multipliercode = feature.multipliercode, excludeInAll = false,
                        type = "feature", discount = 0, totalamount = feature.defaultprice.toInt(), price = feature.price, defaultprice = feature.defaultprice))
                        isAdded = true
                    }
                }
            }
        }

        val licensingParameters: ArrayList<LicensingParameter> = ArrayList()

        for (multiplier in baseModule.multipliers) {
            if (multiplier.slabs.size == multiplier.slabIndex) {
                if (multiplier.customValue.isNullOrBlank()) {
                    multiplier.slabIndex = 0
                } else {
                    licensingParameters.add(LicensingParameter(multiplier.code, multiplier.customValue, 0))
                    continue
                }
            }

            val slabIndex = multiplier.slabIndex
            if (multiplier.slabs.isNullOrEmpty()) continue
            val slabLabel = multiplier.slabLabels[slabIndex]
            licensingParameters.add(LicensingParameter(multiplier.code, slabLabel, 0))
        }

        if (selectedBaseModulePosition == 0) {
            viewModel.softwareLicenseModuleMap[baseModule.code ?: ""] = SummaryResponseSoftwareLicenseModule(licensingparameters = licensingParameters,
                name = baseModule.name, code = baseModule.code, description = "", selfcode = "", defaultprice = 0, totalamount = summaryModuleTotalPrice,
                discount = 0, features = summaryModuleFeatureList, multiplier = "", price = null, excludeInAll = false)
        } else {
            if (isAdded) {
                viewModel.summaryMap[baseModule.code ?: ""] = ModuleGroupSummary(baseModule.code ?: "", baseModule.name ?: "", price)

                viewModel.softwareLicenseModuleMap[baseModule.code ?: ""] = SummaryResponseSoftwareLicenseModule(licensingparameters = licensingParameters,
                name = baseModule.name, code = baseModule.code, description = "", selfcode = "", defaultprice = 0, totalamount = summaryModuleTotalPrice,
                discount = 0, features = summaryModuleFeatureList, multiplier = "", price = null, excludeInAll = false)

                viewModel.moduleChangeMapNew[baseModule.code] = price
            } else {
                viewModel.summaryMap.remove(key = baseModule.code)
                viewModel.softwareLicenseModuleMap.remove(key = baseModule.code)

                viewModel.moduleChangeMapNew.remove(key = baseModule.code)
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
        multiplierListAdapter.submitList(baseModule.multipliers, baseModule.code, viewModel.costSoftwareCustomization, viewModel.costCustomizedReport)
    }

    private fun calculateSummaryCost(moduleCost: Int) {
        val totalUsers = ceil((moduleCost.toDouble() / AppConstants.perUserCost)).toInt()
        viewModel.additionalUsers = if (totalUsers <= AppConstants.additionalUsers) AppConstants.additionalUsers - totalUsers else 0 //abs((AppConstants.additionalUsers - totalUsers))
        viewModel.usersIncluded = totalUsers //abs((AppConstants.additionalUsers - totalUsers)) //if (totalUsers >= AppConstants.additionalUsers) totalUsers - AppConstants.additionalUsers else totalUsers
        viewModel.costAdditionalUsers = (AppConstants.costAdditionalUsers / AppConstants.additionalUsers) * viewModel.additionalUsers
        viewModel.costSoftwareLicense = moduleCost + viewModel.costAdditionalUsers

        viewModel.costRequirementAnalysis = (viewModel.costSoftwareLicense * AppConstants.percentRequirementAnalysis) / 100
        viewModel.costDeployment = (viewModel.costSoftwareLicense * AppConstants.percentDeployment) / 100
        viewModel.costConfiguration = (viewModel.costSoftwareLicense * AppConstants.percentConfiguration) / 100
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

    private fun calculateModuleAndFeaturePrice(index: Int, code: String, customValue: String) {
        if (viewModel.baseModuleList[selectedBaseModulePosition].moduleGroups.isNullOrEmpty()) return

        for (groupIndex in viewModel.baseModuleList[selectedBaseModulePosition].moduleGroups.indices) {

            if (viewModel.baseModuleList[selectedBaseModulePosition].moduleGroups[groupIndex].modules.isNullOrEmpty()) continue

            for (moduleIndex in viewModel.baseModuleList[selectedBaseModulePosition].moduleGroups[groupIndex].modules.indices) {
                val moduleMultiplierCode = viewModel.baseModuleList[selectedBaseModulePosition].moduleGroups[groupIndex].modules[moduleIndex].multiplier
                if (moduleMultiplierCode is String && moduleMultiplierCode == code) {
                    calculateModulePrice(selectedBaseModulePosition, groupIndex, moduleIndex, index, customValue)
                }

                if (viewModel.baseModuleList[selectedBaseModulePosition].moduleGroups[groupIndex].modules[moduleIndex].features.isNullOrEmpty()) continue

                for (featureIndex in viewModel.baseModuleList[selectedBaseModulePosition].moduleGroups[groupIndex].modules[moduleIndex].features.indices) {
                    val featureMultiplierCode = viewModel.baseModuleList[selectedBaseModulePosition].moduleGroups[groupIndex].modules[moduleIndex].features[featureIndex].multiplier
                    if (featureMultiplierCode is String && moduleMultiplierCode == code) {
                        calculateFeaturePrice(selectedBaseModulePosition, groupIndex, moduleIndex, featureIndex, index, customValue)
                    }
                }
            }
        }
    }

    private fun calculateModulePrice(baseIndex: Int, groupIndex: Int, moduleIndex: Int, index: Int, customValue: String) {
        val module = viewModel.baseModuleList[baseIndex].moduleGroups[groupIndex].modules[moduleIndex]
        if (index == -1) {
            try {
                if (customValue.isBlank()) {
                    viewModel.baseModuleList[baseIndex].moduleGroups[groupIndex].modules[moduleIndex].defaultprice = 0.0
                } else {
                    viewModel.baseModuleList[baseIndex].moduleGroups[groupIndex].modules[moduleIndex].defaultprice = customValue.toDouble()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return
        }
        if (module.price.size <= index) return
        val price = module.price[index]
        if (price.isBlank()) return
        try {
            viewModel.baseModuleList[baseIndex].moduleGroups[groupIndex].modules[moduleIndex].defaultprice = price.toDouble()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun calculateFeaturePrice(baseIndex: Int, groupIndex: Int, moduleIndex: Int, featureIndex: Int, index: Int, customValue: String) {
        val feature = viewModel.baseModuleList[baseIndex].moduleGroups[groupIndex].modules[moduleIndex].features[featureIndex]
        if (index == -1) {
            try {
                if (customValue.isBlank()) {
                    viewModel.baseModuleList[baseIndex].moduleGroups[groupIndex].modules[moduleIndex].features[featureIndex].defaultprice = 0.0
                } else {
                    viewModel.baseModuleList[baseIndex].moduleGroups[groupIndex].modules[moduleIndex].features[featureIndex].defaultprice = customValue.toDouble()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return
        }

        if (feature.price.size <= index) return
        val price = feature.price[index]
        if (price.isBlank()) return
        try {
            viewModel.baseModuleList[baseIndex].moduleGroups[groupIndex].modules[moduleIndex].features[featureIndex].defaultprice = price.toDouble()
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

    private fun goHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    private fun goBack() {
        finish()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}