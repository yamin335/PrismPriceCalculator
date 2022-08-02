package net.store.divineit.ui

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.store.divineit.R
import net.store.divineit.databinding.FeatureListItemBinding
import net.store.divineit.models.Feature
import net.store.divineit.utils.toShortForm

class FeatureListAdapter internal constructor(
    private var dataList: List<Feature>,
    private val callback: (Feature) -> Unit
) : RecyclerView.Adapter<FeatureListAdapter.ViewHolder>() {

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
            val mContext = binding.root.context
            val item = dataList[position]
            binding.item = item

            var slabPrice = 0
            if (item.slabPrice == 0) {
                val slab1 = item.price?.slab1
                if (slab1 != null) {
                    try {
                        slabPrice = slab1.toInt()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                slabPrice = item.slabPrice
            }

            if (slabPrice != 0) {
                binding.linearPrice.visibility = View.VISIBLE
                binding.btnAdd.visibility = View.VISIBLE
                val priceText = "৳$slabPrice"
                binding.price.text = priceText
            } else {
                binding.linearPrice.visibility = View.GONE
                binding.btnAdd.visibility = View.GONE
            }

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