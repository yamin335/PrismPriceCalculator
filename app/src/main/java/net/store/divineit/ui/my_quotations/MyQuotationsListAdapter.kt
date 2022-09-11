package net.store.divineit.ui.my_quotations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.store.divineit.R
import net.store.divineit.databinding.MyQuotationsListItemBinding
import net.store.divineit.models.MyQuotation
import net.store.divineit.utils.DateFormat
import net.store.divineit.utils.DateFormatterUtils

class MyQuotationsListAdapter internal constructor(
    private val callback: (MyQuotation) -> Unit
) : RecyclerView.Adapter<MyQuotationsListAdapter.ViewHolder>() {

    private var dataList: List<MyQuotation> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: MyQuotationsListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.list_item_my_quotations, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun submitList(dataList: List<MyQuotation>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: MyQuotationsListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = dataList[position]

            binding.quotationId.text = if (item.quotationid.isNullOrBlank()) {
                "${binding.root.context.getString(R.string.id)}: ${binding.root.context.getString(R.string.n_a)}"
            } else {
                "${binding.root.context.getString(R.string.id)}: ${item.quotationid}"
            }

            binding.dateTime.text = if (item.date.isNullOrBlank()) {
                ""
            } else {
                var dateString: String = item.date
                val date = DateFormatterUtils.formatDateFromString(dateString, DateFormat.format1)
                date?.let {
                    dateString = DateFormatterUtils.formatDateToString(it, DateFormat.format2) ?: ""
                    val dateTime = dateString.split(" ")
                    if (dateTime.size == 2) {
                        dateString = "${dateTime[0]}\n${binding.root.context.getString(R.string.time)}:${dateTime[1]}"
                    }
                }
                "${binding.root.context.getString(R.string.date)}: $dateString"
            }

            binding.productId.text = if (item.productid.isNullOrBlank()) {
                "${binding.root.context.getString(R.string.product_id)}: ${binding.root.context.getString(R.string.n_a)}"
            } else {
                "${binding.root.context.getString(R.string.product_id)}: ${item.productid}"
            }

            binding.customerName.text = if (item.customername.isNullOrBlank()) {
                "${binding.root.context.getString(R.string.customer)}: ${binding.root.context.getString(R.string.n_a)}"
            } else {
                "${binding.root.context.getString(R.string.customer)}: ${item.customername}"
            }

            binding.companyName.text = if (item.company.isNullOrBlank()) {
                "${binding.root.context.getString(R.string.company)}: ${binding.root.context.getString(R.string.n_a)}"
            } else {
                "${binding.root.context.getString(R.string.company)}: ${item.company}"
            }

            binding.status.text = if (item.status.isNullOrBlank()) {
                "${binding.root.context.getString(R.string.status)}: ${binding.root.context.getString(R.string.n_a)}"
            } else {
                "${binding.root.context.getString(R.string.status)}: ${item.status}"
            }

            binding.discount.text = if (item.discount == null) {
                "${binding.root.context.getString(R.string.discount)}: 0 ${binding.root.context.getString(R.string.bdt)}"
            } else {
                "${binding.root.context.getString(R.string.discount)}: ${item.discount} ${binding.root.context.getString(R.string.bdt)}"
            }

            binding.totalCost.text = if (item.totalamount == null) {
                "${binding.root.context.getString(R.string.total_cost)}: 0 ${binding.root.context.getString(R.string.bdt)}"
            } else {
                "${binding.root.context.getString(R.string.total_cost)}: ${item.totalamount} ${binding.root.context.getString(R.string.bdt)}"
            }

            binding.rootCard.setOnClickListener {
                callback(item)
            }
        }
    }
}