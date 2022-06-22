package net.store.divineit.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.store.divineit.R
import net.store.divineit.databinding.MultipliersListItemBinding
import net.store.divineit.models.MultiplierClass

class MultiplierListAdapter internal constructor(
    private var dataList: List<MultiplierClass>,
    private val callback: (MultiplierClass) -> Unit
) : RecyclerView.Adapter<MultiplierListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: MultipliersListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_module_multipliers_type_slider, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(private val binding: MultipliersListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val mContext = binding.root.context
            val item = dataList[position]
            binding.item = item
        }
    }
}