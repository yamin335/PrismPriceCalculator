package net.store.divineit.ui.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.store.divineit.R
import net.store.divineit.api.ApiCallStatus
import net.store.divineit.api.ResponseCodes.CODE_SUCCESS
import net.store.divineit.models.*
import net.store.divineit.repo.HomeRepository
import net.store.divineit.ui.base.BaseViewModel
import net.store.divineit.utils.*
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    @ApplicationContext private val application: Context,
    private val homeRepository: HomeRepository
) : BaseViewModel(application) {
    var costSoftwareLicense = 0
    var costAdditionalUsers = 0
    var additionalUsers = AppConstants.additionalUsers
    var usersIncluded = 0
    var costImplementation = 0
    var costRequirementAnalysis = 0
    var costDeployment = 0
    var costConfiguration = 0
    var costOnsiteAdoptionSupport = 0
    var costTraining = 0
    var costProjectManagement = 0
    var costSoftwareCustomizationTotal = 0
    var costSoftwareCustomization = 0
    var costCustomizedReport = 0
    var costConsultancyServices = 0
    var costConsultancy = 0
    var costAnnualMaintenanceTotal = 0
    var costAnnualMaintenance = 0
    var costTotal = 0

    var responsibleMultipliersOfBaseModules: HashMap<String, HashMap<String, Boolean>> = HashMap()

    val summaryMap: HashMap<String, ModuleGroupSummary> = HashMap()
    val softwareLicenseModuleMap: HashMap<String, SummaryResponseSoftwareLicenseModule> = HashMap()

    val quotationSubmitResponse: MutableLiveData<SummaryResponseQuotation> by lazy {
        MutableLiveData<SummaryResponseQuotation>()
    }

    val baseModuleListTemp: MutableLiveData<List<BaseServiceModule>> by lazy {
        MutableLiveData<List<BaseServiceModule>>()
    }

    val quotationDetailsResponse: MutableLiveData<Pair<SummaryResponseQuotation?, List<BaseServiceModule>?>> by lazy {
        MutableLiveData<Pair<SummaryResponseQuotation?, List<BaseServiceModule>?>>()
    }

    var baseModuleList: ArrayList<BaseServiceModule> = ArrayList()

    var moduleChangeMapOld: HashMap<String?, Int?> = HashMap()
    var moduleChangeMapNew: HashMap<String?, Int?> = HashMap()

    var quotationDetails: SummaryResponseQuotation? = null

    fun productDetails(productId: String) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(application.getString(R.string.commonErrorMessage))
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(homeRepository.productDetails(productId))) {
                    is ApiSuccessResponse -> {
                        when(apiResponse.body.code) {
                            CODE_SUCCESS -> {
                                val baseModules = apiResponse.body.data?.Modules
                                baseModuleList = baseModules as ArrayList<BaseServiceModule>
                                prepareResponsibleMultipliersList()
                                apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                                baseModuleListTemp.postValue(baseModuleList)
                            }
                            else -> {
                                toastError.postValue("Failed! Please try again.")
                            }
                        }
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                        toastError.postValue("Failed! Please try again.")
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                        toastError.postValue("Failed! Please try again.")
                    }
                }
            }
        }
    }

    fun quotationDetails(productId: String, quotationId: String) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(application.getString(R.string.commonErrorMessage))
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                val quotationDetailsFlow = flowOf(ApiResponse.create(homeRepository.quotationDetails(quotationId)))
                val productDetailsFlow = flowOf(ApiResponse.create(homeRepository.productDetails(productId)))

                quotationDetailsFlow.zip(productDetailsFlow) { quotationDetailsResponse, productDetailsResponse ->
                    var result: Pair<SummaryResponseQuotation?, List<BaseServiceModule>?>? = null
                    if (quotationDetailsResponse is ApiSuccessResponse && productDetailsResponse is ApiSuccessResponse) {
                        if (quotationDetailsResponse.body.code == CODE_SUCCESS && productDetailsResponse.body.code == CODE_SUCCESS) {
                            result = Pair(quotationDetailsResponse.body.data?.QuotationSummary, productDetailsResponse.body.data?.Modules)
                        }
                    }
                    return@zip result
                }.flowOn(Dispatchers.IO).catch { exception ->
                    apiCallStatus.postValue(ApiCallStatus.ERROR)
                    toastError.postValue(exception.localizedMessage ?: application.getString(R.string.commonErrorMessage))
                }.collect { result ->
                    baseModuleList = result?.second as ArrayList<BaseServiceModule>
                    prepareResponsibleMultipliersList()
                    quotationDetailsResponse.postValue(result)
                    apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                }

            }
        }
    }

    private fun submitQuotation(summaryStoreBody: SummaryStoreModel) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(application.getString(R.string.commonErrorMessage))
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(homeRepository.submitQuotation(summaryStoreBody))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        when(apiResponse.body.code) {
                            2000001 -> {
                                quotationSubmitResponse.postValue(apiResponse.body.data?.quotation)
                                toastSuccess.postValue("Submission Successful!")
                            }
                            else -> {
                                toastError.postValue("Submission Failed! Please check your credentials and try again.")
                            }
                        }
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                        toastError.postValue("Submission Failed! Please check your credentials and try again.")
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                        toastError.postValue("Submission Failed! Please check your credentials and try again.")
                    }
                }
            }
        }
    }

    private fun updateQuotation(summaryUpdateBody: SummaryResponseQuotation) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(application.getString(R.string.commonErrorMessage))
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(homeRepository.quotationUpdate(summaryUpdateBody))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        when(apiResponse.body.code) {
                            2000001 -> {
                                quotationSubmitResponse.postValue(apiResponse.body.data?.Quotation)
                                toastSuccess.postValue("Successful Updated!")
                            }
                            else -> {
                                toastError.postValue("Submission Failed! Please check your credentials and try again.")
                            }
                        }
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                        toastError.postValue("Submission Failed! Please check your credentials and try again.")
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                        toastError.postValue("Submission Failed! Please check your credentials and try again.")
                    }
                }
            }
        }
    }

    private fun prepareResponsibleMultipliersList() {
        if (!baseModuleList.isNullOrEmpty()) {
            val baseMultiplierMap: HashMap<String, HashMap<String, Boolean>> = HashMap()
            for (baseModuleIndex in baseModuleList.indices) {
                //Configure multiplier labels for every multiplier of a base module
                prepareMultiplierLabels(baseModuleIndex)
                val multiplierMap: HashMap<String, Boolean> = HashMap()
                for (moduleGroupIndex in baseModuleList[baseModuleIndex].moduleGroups.indices) {
                    for (moduleIndex in baseModuleList[baseModuleIndex].moduleGroups[moduleGroupIndex].modules.indices) {
                        val moduleMultiplier = baseModuleList[baseModuleIndex].moduleGroups[moduleGroupIndex].modules[moduleIndex].multiplier
                        if (!moduleMultiplier.isNullOrBlank()) multiplierMap[moduleMultiplier] = true

                        for (featureIndex in baseModuleList[baseModuleIndex].moduleGroups[moduleGroupIndex].modules[moduleIndex].features.indices) {
                            val featureMultiplier = baseModuleList[baseModuleIndex].moduleGroups[moduleGroupIndex].modules[moduleIndex].features[featureIndex].multiplier
                            if (!featureMultiplier.isNullOrBlank()) multiplierMap[featureMultiplier] = true
                        }
                    }
                }
                val baseModuleCode = baseModuleList[baseModuleIndex].code
                if (!baseModuleCode.isNullOrBlank()) baseMultiplierMap[baseModuleCode] = multiplierMap
            }
            responsibleMultipliersOfBaseModules = baseMultiplierMap
        }
    }

    private fun prepareMultiplierLabels(baseModuleIndex: Int) {
        for (multiplierIndex in baseModuleList[baseModuleIndex].multipliers.indices) {
            val multiplier = baseModuleList[baseModuleIndex].multipliers[multiplierIndex]
            if (multiplier.slabConfig?.inputType == "slider") continue
            val slabLabelList: ArrayList<String> = ArrayList()
            for ((index, slab) in multiplier.slabs.withIndex()) {
                val isNumber = slab.matches("((\\d+\\.?)*\\d*)".toRegex())
                val slabLabel = if (isNumber) {
                    if (multiplier.slabConfig?.showRange == true) {
                        var prefix = ""
                        if (multiplier.slabTexts.size > index) {
                            prefix = multiplier.slabTexts[index]
                        }

                        val increment = 1
                        var startItem = increment

                        if (index > 0) {
                            try {
                                val previousItem = multiplier.slabs[index - 1].toDouble().toInt()
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
                            slabText
                        } catch (e: Exception) {
                            e.printStackTrace()
                            slab
                        }
                    } else {
                        var prefix = ""
                        if (multiplier.slabTexts.size > index) {
                            prefix = multiplier.slabTexts[index]
                        }

                        try {
                            val slabPrice = slab.toDouble().toInt()
                            val slabText = if (prefix.isBlank()) {
                                "$slabPrice"
                            } else {
                                "$prefix(${slabPrice})"
                            }
                            slabText
                        } catch (e: Exception) {
                            e.printStackTrace()
                            slab
                        }
                    }
                } else {
                    slab
                }

                slabLabelList.add(index, slabLabel)
            }
            baseModuleList[baseModuleIndex].multipliers[multiplierIndex].slabLabels = slabLabelList
        }
    }

    fun submitOrUpdateSummary(summaryResponseQuotation: SummaryResponseQuotation?) {
        val maintenance = SummaryResponseAdditionalService(
            summeryid = null,
            header = "Annual Maintenance Cost",
            total = costAnnualMaintenanceTotal,
            discount = 0,
            modules = arrayListOf(
                SummaryResponseAdditionalServiceModule(
                    summeryid = null,
                    totalamount = costAnnualMaintenance,
                    discount = 0,
                    details = "",
                    name = "Annual Maintenance Cost",
                    details_value = 0,
                    details_multiplier = 0,
                    Table = ""
                )
            ),
            Table = ""
        )

        val consultancy = SummaryResponseAdditionalService(
            summeryid = null,
            header = "Consultancy Services",
            total = costConsultancyServices,
            discount = 0,
            modules = arrayListOf(
                SummaryResponseAdditionalServiceModule(
                    summeryid = null,
                    totalamount = costConsultancy,
                    discount = 0,
                    details = "man-days x ৳",
                    name = "Consultancy",
                    details_value = 0,
                    details_multiplier = 20000,
                    Table = ""
                )
            ),
            Table = ""
        )

        val customization = SummaryResponseAdditionalService(
            summeryid = null,
            header = "Software Customization", 
            total = costSoftwareCustomizationTotal,
            discount = 0,
            modules = arrayListOf(
                SummaryResponseAdditionalServiceModule(
                    summeryid = null,
                    totalamount = costSoftwareCustomization,
                    discount = 0,
                    details = "man-days x ৳",
                    name = "Software Customization",
                    details_value = costSoftwareCustomization / AppConstants.unitPriceSoftwareCustomization,
                    details_multiplier = AppConstants.unitPriceSoftwareCustomization,
                    Table = ""
                ),
                SummaryResponseAdditionalServiceModule(
                    summeryid = null,
                    totalamount = costCustomizedReport,
                    discount = 0,
                    details = "man-days x ৳",
                    name = "Customized Report",
                    details_value = costCustomizedReport / AppConstants.unitPriceCustomizedReports,
                    details_multiplier = AppConstants.unitPriceCustomizedReports,
                    Table = ""
                )
            ),
            Table = ""
        )

        val implementation = SummaryResponseAdditionalService(
            summeryid = null,
            header = "Implementation",
            total = costImplementation,
            discount = 0,
            modules = arrayListOf(
                SummaryResponseAdditionalServiceModule(
                    summeryid = null,
                    totalamount = costRequirementAnalysis,
                    discount = 0,
                    details = "man-days x ৳",
                    name = "Requirement Analysis",
                    details_value = 0,
                    details_multiplier = 10000,
                    Table = ""
                ),
                SummaryResponseAdditionalServiceModule(
                    summeryid = null,
                    totalamount = costImplementation,
                    discount = 0,
                    details = "(onetime) x ৳",
                    name = "Deployment",
                    details_value = 1,
                    details_multiplier = 10000,
                    Table = ""
                ),
                SummaryResponseAdditionalServiceModule(
                    summeryid = null,
                    totalamount = costConfiguration,
                    discount = 0,
                    details = "man-days x ৳",
                    name = "Configuration",
                    details_value = 0,
                    details_multiplier = 10000,
                    Table = ""
                ),
                SummaryResponseAdditionalServiceModule(
                    summeryid = null,
                    totalamount = costOnsiteAdoptionSupport,
                    discount = 0,
                    details = "man-days x ৳",
                    name = "Onsite Adoption Support",
                    details_value = 0,
                    details_multiplier = 6000,
                    Table = ""
                ),
                SummaryResponseAdditionalServiceModule(
                    summeryid = null,
                    totalamount = costTraining,
                    discount = 0,
                    details = "sessions x ৳",
                    name = "Training",
                    details_value = 0,
                    details_multiplier = 6000,
                    Table = ""
                ),
                SummaryResponseAdditionalServiceModule(
                    summeryid = null,
                    totalamount = costProjectManagement,
                    discount = 0,
                    details = "man-days x ৳",
                    name = "Project Management",
                    details_value = 0,
                    details_multiplier = 1200,
                    Table = ""
                )
            ),
            Table = ""
        )

        var totalAmount = (maintenance.total ?: 0) + (consultancy.total ?: 0) +
        (customization.total ?: 0) + (implementation.total ?: 0)

        val softwareLicenseModuleList: ArrayList<SummaryResponseSoftwareLicenseModule> = ArrayList()

        for (key in softwareLicenseModuleMap.keys) {
            val softwareLicenseModule = softwareLicenseModuleMap[key]
            if (softwareLicenseModule != null) {
                softwareLicenseModuleList.add(softwareLicenseModule)
                totalAmount += softwareLicenseModule.totalamount ?: 0
            }
        }

        val summarySoftwareLicense = SummaryResponseSoftwareLicense(summeryid = null, header = "Software License", totalamount = totalAmount,
        discount = 0, users = usersIncluded, additionalusers = additionalUsers, modules = softwareLicenseModuleList, Table = "")

        if (summaryResponseQuotation == null) {
            val summaryStoreBody = SummaryStoreModel(
                salesmanid = preferencesHelper.userAccount?.salesmanid,
                customerid = preferencesHelper.userAccount?.id, details = false,
                header = "Summery", productid = "prismerp",
                totalamount = totalAmount, Software_License = summarySoftwareLicense,
                Implementation = implementation, Customization = customization,
                Consultancy = consultancy, Maintainance = maintenance, company = "RTC Hubs")
            submitQuotation(summaryStoreBody = summaryStoreBody)
        } else {
            summaryResponseQuotation.totalamount = totalAmount
            summaryResponseQuotation.Software_License = summarySoftwareLicense
            summaryResponseQuotation.Implementation = implementation
            summaryResponseQuotation.Customization = customization
            summaryResponseQuotation.Consultancy = consultancy
            summaryResponseQuotation.Maintainance = maintenance

            updateQuotation(summaryUpdateBody = summaryResponseQuotation)
        }
    }
}