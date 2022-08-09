package net.store.divineit.models

data class ServiceModuleResponse ( val code: Int?, val data: ServiceModuleResponseData?, val msg: String?)

data class ServiceModuleResponseData(val Modules: List<BaseServiceModule>?)

data class BaseServiceModule (
    val code: String?,
    val name: String?,
    val moduleGroups: List<ModuleGroup>,
    var multipliers: List<MultiplierClass>
)

data class ModuleGroup (
    val name: String?,
    val code: String?,
    val ModuleStartIndex: Int?,
    val ModuleEndIndex: Int?,
    val modules: List<ServiceModule>,
    val dependencies: List<Any?>,
    val showMultiplier: String?,
    val description: String?,
    var isExpanded: Boolean = false
)

data class ServiceModule (
    val licensingparameters: Any?,
    val name: String?,
    val code: String?,
    val description: String?,
    val selfCode: String?,
    val totalamount: Int?,
    val discount: Int?,
    val features: List<Feature>,
    val dependencies: List<String>,
    val multiplier: String?,
    val price: List<String>,
    val excludeInAll: Boolean?,
    var isAdded: Boolean = false,
    var defaultprice: Double = 0.0
)

data class Feature (
    val name: String,
    val code: String,
    val parentcode: String?,
    val description: String?,
    val multipliercode: String?,
    val type: String?,
    val excludeInAll: Boolean?,
    val discount: Int?,
    val multiplier: String?,
    val price: List<String>,
    var isAdded: Boolean = false,
    var defaultprice: Double = 0.0
)

data class MultiplierClass (
    val name: String?,
    val code: String?,
    val label: String?,
    val slabConfig: SlabConfig?,
    val slabs: List<String>,
    val slabTexts: List<String>,
    var slabIndex: Int = 0
)

data class SlabConfig (
    val inputType: String?,
    val showSlabs: Boolean?,
    val className: String?,
    val hideInApp: Boolean?,
    val showRange: Boolean?,
    val customUser: Boolean?,
    val help: String?,
    val increment: Int?
)

data class SubModule (
    val name: String,
    val code: String,
    val features: List<Feature> = ArrayList(),
    val description: String? = null,
    val dependencies: List<Any?>
)

data class ModuleGroupSummary (
    val code: String,
    val title: String,
    val price: Int
)