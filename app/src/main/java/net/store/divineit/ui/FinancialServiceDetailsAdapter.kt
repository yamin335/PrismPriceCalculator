package net.store.divineit.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.store.divineit.R
import net.store.divineit.databinding.FinancialServiceListDetailsItemBinding

class FinancialServiceDetailsAdapter internal constructor(
    private val callback: (String) -> Unit
) : RecyclerView.Adapter<FinancialServiceDetailsAdapter.ViewHolder>() {

    private var dataList: ArrayList<String> = ArrayList()

    private var selected = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: FinancialServiceListDetailsItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_financial_service_details, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun submitList(dataList: List<String>) {
        this.dataList = dataList  as ArrayList<String>
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: FinancialServiceListDetailsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.item = dataList[position]
        }
    }
}