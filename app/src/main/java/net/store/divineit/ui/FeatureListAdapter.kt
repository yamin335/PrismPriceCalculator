package net.store.divineit.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.store.divineit.R
import net.store.divineit.databinding.FeatureListItemBinding
import net.store.divineit.models.Feature
import net.store.divineit.utils.toShortForm

class FeatureListAdapter internal constructor(
    private val callback: (Feature) -> Unit
) : RecyclerView.Adapter<FeatureListAdapter.ViewHolder>() {

    private var dataList: ArrayList<Feature> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: FeatureListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_feature, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun submitList(dataList: List<Feature>) {
        this.dataList = dataList  as ArrayList<Feature>
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: FeatureListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = dataList[position]
            binding.item = item
        }
    }
}