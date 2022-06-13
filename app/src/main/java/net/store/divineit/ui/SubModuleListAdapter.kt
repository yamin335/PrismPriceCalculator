package net.store.divineit.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.store.divineit.R
import net.store.divineit.databinding.SubModuleListItemBinding
import net.store.divineit.models.Feature
import net.store.divineit.models.SubModule
import net.store.divineit.utils.toShortForm

class SubModuleListAdapter internal constructor(
    private var dataList: List<SubModule>,
    private val callback: (SubModule) -> Unit
) : RecyclerView.Adapter<SubModuleListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: SubModuleListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_sub_module, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun submitList(dataList: List<SubModule>) {
        this.dataList = dataList  as ArrayList<SubModule>
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: SubModuleListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = dataList[position]
            binding.item = item

            val featureListAdapter = FeatureListAdapter(dataList[position].features ?: ArrayList()) {
                callback(dataList[position])
            }

            val innerLLM = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
            innerLLM.initialPrefetchItemCount = 3

            binding.recyclerFeatures.apply {
                isNestedScrollingEnabled = false
                setHasFixedSize(true)
                layoutManager = innerLLM
                adapter = featureListAdapter
            }
            //featureListAdapter.submitList(item.features ?: ArrayList())
        }
    }
}