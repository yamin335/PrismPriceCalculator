package net.store.divineit.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.store.divineit.R
import net.store.divineit.databinding.BaseServiceModuleListItemBinding
import net.store.divineit.models.BaseServiceModule

class BaseServiceModuleListAdapter internal constructor(
    private var dataList: List<BaseServiceModule>,
    private val callback: (BaseServiceModule, Int) -> Unit
) : RecyclerView.Adapter<BaseServiceModuleListAdapter.ViewHolder>() {
    private var selectedItemPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: BaseServiceModuleListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_base_service_module, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun submitList(dataList: List<BaseServiceModule>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: BaseServiceModuleListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val context = binding.root.context
            val item = dataList[position]
            binding.item = item
            binding.root.setOnClickListener {
                val previousPosition = selectedItemPosition
                selectedItemPosition = position
                callback(dataList[position], position)
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedItemPosition)
            }

            if (position == selectedItemPosition) {
                binding.container.background = ContextCompat.getDrawable(context, R.drawable.rounded_rectangle_blue_3)
                binding.name.setTextColor(ContextCompat.getColor(context, R.color.black))
            } else {
                binding.container.background = ContextCompat.getDrawable(context, R.drawable.rounded_rectangle_gray_2)
                binding.name.setTextColor(ContextCompat.getColor(context, R.color.blue3))
            }
        }
    }
}