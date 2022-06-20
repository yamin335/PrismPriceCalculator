package net.store.divineit.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.store.divineit.R
import net.store.divineit.databinding.ModuleGroupListItemBinding
import net.store.divineit.models.Feature
import net.store.divineit.models.ModuleGroup

class ModuleGroupAdapter internal constructor(
    private val baseModuleCode: String,
    private var dataList: List<ModuleGroup>,
    private val callback: (ModuleGroup) -> Unit
) : RecyclerView.Adapter<ModuleGroupAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ModuleGroupListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_module_group, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun submitList(dataList: List<ModuleGroup>) {
        this.dataList = dataList  as ArrayList<ModuleGroup>
        notifyDataSetChanged()
    }

    fun notifyModuleItemChanged(moduleGroupListItemPosition: Int) {
        notifyItemChanged(moduleGroupListItemPosition)
    }

    inner class ViewHolder(private val binding: ModuleGroupListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {

            val mContext = binding.root.context
            val item = dataList[position]
            binding.item = item

            if (baseModuleCode == "START") {
                binding.numberOfModules.visibility = View.GONE
                binding.linearShowHide.visibility = View.GONE
                binding.linearActions.visibility = View.GONE
                binding.expandableLayout.isExpanded = true
            } else {
                binding.numberOfModules.visibility = View.VISIBLE
                binding.linearShowHide.visibility = View.VISIBLE
                binding.linearActions.visibility = View.VISIBLE
                binding.expandableLayout.isExpanded = item.isExpanded
            }

            var numberOfSelectedModules = 0
            for (module in dataList[position].modules) {
                if (module.isAdded) {
                    numberOfSelectedModules += 1
                }
            }

            val selectedModuleText: String = if (numberOfSelectedModules > 0) {
                binding.numberOfSelectedModules.background = ContextCompat.getDrawable(mContext, R.drawable.rounded_rectangle_green_3)
                binding.numberOfSelectedModules.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                "$numberOfSelectedModules of ${item.modules.size} selected"
            } else {
                binding.numberOfSelectedModules.background = ContextCompat.getDrawable(mContext, R.drawable.rounded_rectangle_yellow_1)
                binding.numberOfSelectedModules.setTextColor(ContextCompat.getColor(mContext, R.color.black))
                "No module selected"
            }
            binding.numberOfSelectedModules.text = selectedModuleText

            val numberOfModuleText = "${item.modules.size} modules"
            binding.numberOfModules.text = numberOfModuleText

            binding.topBar.setOnClickListener {
                toggleExpanded(item, binding)
                binding.expandableLayout.isExpanded = item.isExpanded
            }

            val moduleListAdapter = ModuleListAdapter(baseModuleCode, dataList[position].modules, {
                callback(dataList[position])
            }, moduleChangeCallback = {
                notifyItemChanged(position)
                callback(dataList[position])
            })
            val innerLLM = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
            innerLLM.initialPrefetchItemCount = 3

            binding.recyclerModule.apply {
                isNestedScrollingEnabled = false
                setHasFixedSize(true)
                layoutManager = innerLLM
                adapter = moduleListAdapter
            }
            //moduleListAdapter.submitList(item.modules)
        }

        private fun toggleExpanded(item: ModuleGroup, binding: ModuleGroupListItemBinding) {
            item.isExpanded = !item.isExpanded
            if (item.isExpanded) {
                binding.arrowClickToShow.animate().setDuration(200).rotation(180F)
                binding.clickToShow.text = "Click to hide Modules"
            } else {
                binding.arrowClickToShow.animate().setDuration(200).rotation(0F)
                binding.clickToShow.text = "Click to show Modules"
            }
        }
    }
}