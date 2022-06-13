package net.store.divineit.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.store.divineit.R
import net.store.divineit.databinding.ModuleSummaryListItemBinding
import net.store.divineit.models.ModuleGroupSummary

class ModuleGroupSummaryListAdapter: RecyclerView.Adapter<ModuleGroupSummaryListAdapter.ViewHolder>() {

    private var dataList: ArrayList<ModuleGroupSummary> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ModuleSummaryListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_module_summary, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun submitList(dataList: List<ModuleGroupSummary>) {
        this.dataList = dataList  as ArrayList<ModuleGroupSummary>
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ModuleSummaryListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = dataList[position]
            binding.item = item

            val priceText = "৳${item.price}"
            binding.price.text = priceText
        }
    }
}