package net.store.divineit.models

data class ModuleGroupSummary (
    val code: String,
    val title: String,
    val price: Int
)

data class BaseServiceModule (
    val code: String,
    val name: String,
    val moduleGroups: List<ModuleGroup>
)

data class ModuleGroup (
    val name: String,
    val code: String,
    val modules: List<ServiceModule>,
    val dependencies: List<Any?>,
    val multipliers: List<MultiplierClass>,
    val showMultiplier: String? = null,
    val description: String? = null,
    var isExpanded: Boolean = false
)

data class ServiceModule (
    val code: String,
    val selfCode: String,
    val name: String,
    val submodules: List<SubModule>,
    val features: List<Feature>,
    val description: String? = null,
    val dependencies: List<String>,
    val price: Price? = null,
    val ready: String? = null,
    val showMultiplier: String? = null,
    var isAdded: Boolean = false
)

data class Feature (
    val name: String,
    val code: String,
    val description: String? = null,
    val dependencies: List<String>,
    val price: Price? = null,
    val ready: String? = null,
    val excludeInAll: Long? = null,
    var isAdded: Boolean = false
)

data class SubModule (
    val name: String,
    val code: String,
    val features: List<Feature>? = null,
    val description: String? = null,
    val dependencies: List<Any?>,
    val price: Price? = null
)

data class MultiplierClass (
    val code: String,
    val label: String,
    val slabConfig: SlabConfig,
    val slabs: List<Any>, // Can be long or string type
    val name: String
)

data class SlabConfig (
    val type: String? = null,
    val customUser: Boolean,
    val slabRange: Boolean,
    val hideInApp: Boolean? = null,
    val inputType: String,
    val showSlabs: Boolean,
    val increment: Any,
    val slabTexts: List<SlabText>,
    val className: String? = null,
    val customText: String? = null,
    val showRange: Boolean? = null,
    val help: String? = null,
    val noDefault: Boolean? = null,
    val paramType: String? = null
)

data class SlabText (
    val title: String? = null
)

data class Price (
    val multiplier: Any? = null, // Can be long or string type
    val slab1: Number? = null,
    val slab2: Number? = null,
    val slab3: Number? = null,
    val slab4: Number? = null,
    val slab5: Number? = null,
    val slab6: Number? = null,
    val slab7: Number? = null
)