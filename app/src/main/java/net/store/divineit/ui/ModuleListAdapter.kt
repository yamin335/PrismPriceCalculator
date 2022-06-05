package net.store.divineit.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.store.divineit.R
import net.store.divineit.databinding.ModuleListItemBinding
import net.store.divineit.models.ServiceModule
import net.store.divineit.utils.toShortForm

class ModuleListAdapter internal constructor(
    private val callback: (ServiceModule) -> Unit
) : RecyclerView.Adapter<ModuleListAdapter.ViewHolder>() {

    private var dataList: ArrayList<ServiceModule> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ModuleListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_module, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun submitList(dataList: List<ServiceModule>) {
        this.dataList = dataList  as ArrayList<ServiceModule>
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ModuleListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = dataList[position]
            binding.item = item
            binding.titleShortForm.text = item.name.toShortForm()

            val subModuleListAdapter = SubModuleListAdapter {

            }

            binding.recyclerSubModule.setHasFixedSize(true)

            binding.recyclerSubModule.adapter = subModuleListAdapter
            subModuleListAdapter.submitList(item.submodules)

            val featureListAdapter = FeatureListAdapter {

            }

            binding.recyclerFeatures.setHasFixedSize(true)

            binding.recyclerFeatures.adapter = featureListAdapter
            featureListAdapter.submitList(item.features)
        }
    }
}