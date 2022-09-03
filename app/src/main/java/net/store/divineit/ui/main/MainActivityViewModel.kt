package net.store.divineit.ui.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import net.store.divineit.R
import net.store.divineit.api.ApiCallStatus
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
    val softwareLicenseModuleMap: HashMap<String, SoftwareLicenseModule> = HashMap()

    val quotationSubmitResponse: MutableLiveData<SummaryResponse> by lazy {
        MutableLiveData<SummaryResponse>()
    }

    val baseModuleListTemp: MutableLiveData<List<BaseServiceModule>> by lazy {
        MutableLiveData<List<BaseServiceModule>>()
    }

    var baseModuleList: ArrayList<BaseServiceModule> = ArrayList()

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
                            2000001 -> {
                                val baseModules = apiResponse.body.data?.Modules
                                if (!baseModules.isNullOrEmpty()) {
                                    val baseMultiplierMap: HashMap<String, HashMap<String, Boolean>> = HashMap()
                                    for (baseModule in baseModules) {
                                        val multiplierMap: HashMap<String, Boolean> = HashMap()
                                        for (moduleGroup in baseModule.moduleGroups) {
                                            for (module in moduleGroup.modules) {
                                                val moduleMultiplier = module.multiplier
                                                if (!moduleMultiplier.isNullOrBlank()) multiplierMap[moduleMultiplier] = true

                                                for (feature in module.features) {
                                                    val featureMultiplier = feature.multiplier
                                                    if (!featureMultiplier.isNullOrBlank()) multiplierMap[featureMultiplier] = true
                                                }
                                            }
                                        }
                                        val baseModuleCode = baseModule.code
                                        if (!baseModuleCode.isNullOrBlank()) baseMultiplierMap[baseModuleCode] = multiplierMap
                                    }
                                    responsibleMultipliersOfBaseModules = baseMultiplierMap
                                    apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                                }
                                baseModuleListTemp.postValue(baseModules)
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
                                quotationSubmitResponse.postValue(apiResponse.body)
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

    fun submitSummary() {
        val maintenance = SummaryService(
            header = "Annual Maintenance Cost", 
            totalamount = costAnnualMaintenanceTotal,
            modules = arrayListOf(
                SummaryServiceModule(
                    name = "Annual Maintenance Cost", 
                    details = null,
                    details_value = null, details_multiplier = null,
                    totalamount = costAnnualMaintenance
                )
            )
        )

        val consultancy = SummaryService(
                header = "Consultancy Services", 
            totalamount = costConsultancyServices,
            modules = arrayListOf(
                SummaryServiceModule(
                    name = "Consultancy", 
                    details = " man-days x ৳",
                    details_value = 0, 
                    details_multiplier = 20000,
                    totalamount = costConsultancy
                )
            )
        )

        val customization = SummaryService(
            header = "Software Customization", 
            totalamount = costSoftwareCustomizationTotal,
            modules = arrayListOf(
                SummaryServiceModule(
                    name = "Software Customization", 
                    details = "man-days x ৳",
                    details_value = costSoftwareCustomization / AppConstants.unitPriceSoftwareCustomization,
                    details_multiplier = 16000,
                    totalamount = costSoftwareCustomization
                ),
                SummaryServiceModule(
                    name = "Customized Report", 
                    details = "man-days x ৳",
                    details_value = costCustomizedReport / AppConstants.unitPriceCustomizedReports,
                    details_multiplier = 16000,
                    totalamount = costCustomizedReport
                )
            )
        )

        val implementation = SummaryService(
            header = "Implementation", 
            totalamount = costImplementation,
            modules = arrayListOf(
                SummaryServiceModule(
                    name = "Requirement Analysis",
                    details = "man-days x ৳",
                    details_value = 0,
                    details_multiplier = 10000,
                    totalamount = costRequirementAnalysis
                ),
                SummaryServiceModule(
                    name = "Deployment",
                    details = "(onetime) x ৳",
                    details_value = 1,
                    details_multiplier = 10000,
                    totalamount = costImplementation
                ),
                SummaryServiceModule(
                    name = "Configuration",
                    details = "man-days x ৳",
                    details_value = 0,
                    details_multiplier = 10000,
                    totalamount = costConfiguration
                ),
                SummaryServiceModule(
                    name = "Onsite Adoption Support",
                    details = "man-days x ৳",
                    details_value = 0,
                    details_multiplier = 6000,
                    totalamount = costOnsiteAdoptionSupport
                ),
                SummaryServiceModule(
                    name = "Training",
                    details = "sessions x ৳",
                    details_value = 0,
                    details_multiplier = 6000,
                    totalamount = costTraining
                ),
                SummaryServiceModule(
                    name = "Project Management",
                    details = "man-days x ৳",
                    details_value = 0,
                    details_multiplier = 12000,
                    totalamount = costProjectManagement
                )
            )
        )

        var totalAmount = (maintenance.totalamount ?: 0) + (consultancy.totalamount ?: 0) +
        (customization.totalamount ?: 0) + (implementation.totalamount ?: 0)

        val softwareLicenseModuleList: ArrayList<SoftwareLicenseModule> = ArrayList()

        for (key in softwareLicenseModuleMap.keys) {
            val softwareLicenseModule = softwareLicenseModuleMap[key]
            if (softwareLicenseModule != null) {
                softwareLicenseModuleList.add(softwareLicenseModule)
                totalAmount += softwareLicenseModule.totalamount ?: 0
            }
        }

        val summarySoftwareLicense = SummarySoftwareLicense(additionalusers = additionalUsers, users = usersIncluded,
        header = "Software License", totalamount = totalAmount,
        modules = softwareLicenseModuleList)

        val summaryStoreBody = SummaryStoreModel(
            salesmanid = preferencesHelper.userAccount?.salesmanid,
            customerid = preferencesHelper.userAccount?.id, details = false,
            header = "Summery", productid = "prismerp",
            totalamount = totalAmount, Software_License = summarySoftwareLicense,
            Implementation = implementation, Customization = customization,
            Consultancy = consultancy, Maintainance = maintenance, company = "RTC Hubs")
        submitQuotation(summaryStoreBody = summaryStoreBody)
    }
}