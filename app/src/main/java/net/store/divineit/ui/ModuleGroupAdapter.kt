package net.store.divineit.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.store.divineit.R
import net.store.divineit.databinding.ModuleGroupListItemBinding
import net.store.divineit.models.ModuleGroup

class ModuleGroupAdapter internal constructor(
    private val callback: (ModuleGroup) -> Unit
) : RecyclerView.Adapter<ModuleGroupAdapter.ViewHolder>() {

    private var dataList: ArrayList<ModuleGroup> = ArrayList()

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

    inner class ViewHolder(private val binding: ModuleGroupListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = dataList[position]
            binding.item = item

            val numberOfModuleText = "${item.modules.size} modules"
            binding.numberOfModules.text = numberOfModuleText

            binding.expandableLayout.isExpanded = item.isExpanded

            binding.topBar.setOnClickListener {
                toggleExpanded(item, binding)
                binding.expandableLayout.isExpanded = item.isExpanded
            }

            val moduleListAdapter = ModuleListAdapter {

            }
            val innerLLM = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
            innerLLM.initialPrefetchItemCount = 3

            binding.recyclerModule.apply {
                isNestedScrollingEnabled = false
                setHasFixedSize(true)
                layoutManager = innerLLM
                adapter = moduleListAdapter
            }
            moduleListAdapter.submitList(item.modules)
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