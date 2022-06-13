package net.store.divineit.ui

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.store.divineit.R
import net.store.divineit.databinding.ModuleListItemBinding
import net.store.divineit.models.Feature
import net.store.divineit.models.ServiceModule
import net.store.divineit.utils.toShortForm

class ModuleListAdapter internal constructor(
    private var dataList: List<ServiceModule>,
    private val callback: (ServiceModule) -> Unit
) : RecyclerView.Adapter<ModuleListAdapter.ViewHolder>() {

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
            val mContext = binding.root.context
            val item = dataList[position]
            binding.item = item
            binding.titleShortForm.text = item.name.toShortForm()
            val price = item.price?.slab1
            if (price != null) {
                binding.linearPrice.visibility = View.VISIBLE
                binding.btnAdd.visibility = View.VISIBLE
                val priceText = "৳$price"
                binding.price.text = priceText
            } else {
                binding.linearPrice.visibility = View.GONE
                binding.btnAdd.visibility = View.GONE
            }

            val subModuleListAdapter = SubModuleListAdapter(dataList[position].submodules) {
                callback(dataList[position])
            }
            val innerLLM = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
            innerLLM.initialPrefetchItemCount = 2

            binding.recyclerSubModule.apply {
                isNestedScrollingEnabled = false
                setHasFixedSize(true)
                layoutManager = innerLLM
                adapter = subModuleListAdapter
            }
            //subModuleListAdapter.submitList(item.submodules)

            val featureListAdapter = FeatureListAdapter(dataList[position].features) {
                callback(dataList[position])
            }

            val innerLLM2 = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
            innerLLM2.initialPrefetchItemCount = 3

            binding.recyclerFeatures.apply {
                isNestedScrollingEnabled = false
                setHasFixedSize(true)
                layoutManager = innerLLM2
                adapter = featureListAdapter
            }
            //featureListAdapter.submitList(item.features)

            if (item.isAdded) {
                binding.btnAdd.text = "Remove"
                binding.btnAdd.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.green1))
            } else {
                binding.btnAdd.text = "Add"
                binding.btnAdd.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.blue1))
            }

            binding.btnAdd.setOnClickListener {
                dataList[position].isAdded = !dataList[position].isAdded
                notifyItemChanged(position)
                callback(dataList[position])
            }
        }
    }
}