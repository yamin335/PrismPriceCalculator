package net.store.divineit.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.store.divineit.R
import net.store.divineit.databinding.FinancialServiceListItemBinding
import net.store.divineit.models.FinancialService

class FinancialServiceListAdapter internal constructor(
    private val callback: (FinancialService) -> Unit
) : RecyclerView.Adapter<FinancialServiceListAdapter.ViewHolder>() {

    private var dataList: ArrayList<FinancialService> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: FinancialServiceListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_financial_service, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun submitList(dataList: List<FinancialService>) {
        this.dataList = dataList  as ArrayList<FinancialService>
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: FinancialServiceListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = dataList[position]
            binding.item = item

            binding.numberOfModules.text = "${item.numberOfModules} modules"

            binding.expandableLayout.isExpanded = item.isExpanded

            binding.topBar.setOnClickListener {
                toggleExpanded(item, binding)
                binding.expandableLayout.isExpanded = item.isExpanded
            }

            val listDetailsAdapter = FinancialServiceDetailsAdapter {

            }

            binding.details.setHasFixedSize(true)

            binding.details.adapter = listDetailsAdapter
            listDetailsAdapter.submitList(item.details)
        }

        private fun toggleExpanded(item: FinancialService, binding: FinancialServiceListItemBinding) {
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