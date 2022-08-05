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
import net.store.divineit.repo.LoginRepository
import net.store.divineit.ui.base.BaseViewModel
import net.store.divineit.utils.ApiEmptyResponse
import net.store.divineit.utils.ApiErrorResponse
import net.store.divineit.utils.ApiResponse
import net.store.divineit.utils.ApiSuccessResponse
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    @ApplicationContext private val application: Context,
    private val homeRepository: HomeRepository
) : BaseViewModel(application) {
    var costSoftwareLicense = 0
    var costAdditionalUsers = 150000
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
    
    val summaryMap: HashMap<String, ModuleGroupSummary> = HashMap()
    val softwareLicenseModuleMap: HashMap<String, SoftwareLicenseModule> = HashMap()

    val quotationSubmitResponse: MutableLiveData<SummaryResponse> by lazy {
        MutableLiveData<SummaryResponse>()
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
                    detailsValue = null, detailsMultiplier = null,
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
                    detailsValue = 0, 
                    detailsMultiplier = 20000,
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
                    detailsValue = 0, 
                    detailsMultiplier = 16000,
                    totalamount = costSoftwareCustomization
                ),
                SummaryServiceModule(
                    name = "Customized Report", 
                    details = "man-days x ৳",
                    detailsValue = 0, 
                    detailsMultiplier = 16000,
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
                    detailsValue = 0,
                    detailsMultiplier = 10000,
                    totalamount = 0
                ),
                SummaryServiceModule(
                    name = "Deployment",
                    details = "(onetime) x ৳",
                    detailsValue = 1,
                    detailsMultiplier = 10000,
                    totalamount = costImplementation
                ),
                SummaryServiceModule(
                    name = "Configuration",
                    details = "man-days x ৳",
                    detailsValue = 0,
                    detailsMultiplier = 10000,
                    totalamount = 0
                ),
                SummaryServiceModule(
                    name = "Onsite Adoption Support",
                    details = "man-days x ৳",
                    detailsValue = 0,
                    detailsMultiplier = 6000,
                    totalamount = 0
                ),
                SummaryServiceModule(
                    name = "Training",
                    details = "sessions x ৳",
                    detailsValue = 0,
                    detailsMultiplier = 6000,
                    totalamount = 0
                ),
                SummaryServiceModule(
                    name = "Project Management",
                    details = "man-days x ৳",
                    detailsValue = 0,
                    detailsMultiplier = 12000,
                    totalamount = 0
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

        val summarySoftwareLicense = SummarySoftwareLicense(additionalusers = 0, users = 24,
        header = "Software License", totalamount = totalAmount,
        modules = softwareLicenseModuleList)

        val summaryStoreBody = SummaryStoreModel(salesmanid = preferencesHelper.userAccount?.salesmanid,
        customerid = preferencesHelper.userAccount?.id, details = false,
        header = "Summery", productid = "prismerp",
        totalamount = totalAmount, softwareLicense = summarySoftwareLicense,
        implementation = implementation, customization = customization,
        consultancy = consultancy, maintainance = maintenance)
        submitQuotation(summaryStoreBody = summaryStoreBody)
    }
}