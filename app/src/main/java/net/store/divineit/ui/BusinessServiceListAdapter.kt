package net.store.divineit.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import net.store.divineit.R
import net.store.divineit.databinding.ServiceListItemBinding
import net.store.divineit.models.BusinessService

class BusinessServiceListAdapter internal constructor(
    private var dataList: List<BusinessService>,
    private val callback: (BusinessService) -> Unit
) : RecyclerView.Adapter<BusinessServiceListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ServiceListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_service, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun submitList(dataList: List<BusinessService>) {
        this.dataList = dataList  as ArrayList<BusinessService>
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ServiceListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val mContext = binding.root.context
            val item = dataList[position]
            binding.item = item

            Glide.with(mContext)
                .load(item.icon)
                //.centerCrop()
                //.placeholder(R.drawable.ic_place_holder)
                //.error(R.drawable.ic_place_holder)
                //.apply(RequestOptions().override(350, 366))
                .into(binding.icon)

            val price = item.startingPrice
            val priceText = if (price != null) {
                "৳$price"
            } else {
                "৳0"
            }

            binding.price.text = priceText

            binding.btnViewPricing.setOnClickListener {
                callback(dataList[position])
            }
        }
    }
}