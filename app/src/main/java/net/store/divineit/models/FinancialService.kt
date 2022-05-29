package net.store.divineit.models

data class FinancialService(var serviceName: String = "", var numberOfModules: Int = 0,
                            var numberOfSelectedModules: Int = 0, var isExpanded: Boolean = false,
                            var details: ArrayList<String> = arrayListOf())
