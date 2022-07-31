package net.store.divineit.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.store.divineit.R
import net.store.divineit.api.Api.API_ROOT_URL
import net.store.divineit.databinding.ServiceListItemBinding
import net.store.divineit.models.ServiceProduct

class AllProductsListAdapter internal constructor(
    private val callback: (ServiceProduct) -> Unit
) : RecyclerView.Adapter<AllProductsListAdapter.ViewHolder>() {

    private var dataList: List<ServiceProduct> = ArrayList()
    
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

    fun submitList(dataList: List<ServiceProduct>) {
        this.dataList = dataList  as ArrayList<ServiceProduct>
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ServiceListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val mContext = binding.root.context
            val item = dataList[position]
            binding.item = item

            Glide.with(mContext)
                .load("$API_ROOT_URL${item.logo}")
                //.centerCrop()
                //.placeholder(R.drawable.ic_place_holder)
                //.error(R.drawable.ic_place_holder)
                //.apply(RequestOptions().override(350, 366))
                .into(binding.icon)

            val price = item.price
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