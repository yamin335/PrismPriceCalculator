package net.store.divineit.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import net.store.divineit.R
import net.store.divineit.databinding.MultiplierListItemBinding
import net.store.divineit.models.MultiplierClass

class MultiplierListAdapter internal constructor(
    private val callback: (Int, String, Int) -> Unit
) : RecyclerView.Adapter<MultiplierListAdapter.ViewHolder>() {

    private var dataList: ArrayList<MultiplierClass> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: MultiplierListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_multiplier, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun submitList(dataList: List<MultiplierClass>) {
        this.dataList = dataList  as ArrayList<MultiplierClass>
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: MultiplierListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = dataList[position]
            binding.item = item

            val mContext = binding.root.context
            binding.chipGroup.removeAllViews()

            if (item.slabConfig?.inputType == "slider") {
                binding.linearSlider.visibility = View.VISIBLE
                if (item.slabs.isEmpty()) {
                    binding.linearSlider.visibility = View.GONE
                    return
                }

                var sliderMaxRange = 10.0

                try {
                    sliderMaxRange = item.slabs[0].toDouble()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                binding.slider.valueTo = sliderMaxRange.toFloat()

                binding.maxText.text = "MAX ($sliderMaxRange)"

                val sliderStepSize = item.slabConfig.increment ?: 1

                binding.slider.stepSize = sliderStepSize.toFloat()
            } else {
                binding.linearSlider.visibility = View.GONE
                for ((index, slab) in item.slabs.withIndex()) {
                    var prefix = ""
                    if (item.slabTexts.size > index) {
                        prefix = item.slabTexts[index]
                    }

                    val increment = item.slabConfig?.increment ?: 0
                    var startItem = increment

                    if (index > 0) {
                        val previousItem = item.slabs[index - 1].toDouble().toInt()
                        startItem = previousItem + increment
                    }

                    try {
                        val slabPrice = slab.toDouble().toInt()
                        val slabText = if (prefix.isBlank()) {
                            if (startItem == slabPrice) "$slabPrice" else "$startItem-${slabPrice}"
                        } else {
                            if (startItem == slabPrice) "$prefix(${slabPrice})" else "$prefix($startItem-${slabPrice})"
                        }
                        binding.chipGroup.addView(createTagChip(mContext, index, slabText, item.slabIndex), index)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        binding.chipGroup.addView(createTagChip(mContext, index, slab, item.slabIndex), index)
                    }
                }

                binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
                    if (checkedIds.size == 1) {
                        callback(checkedIds[0], item.code ?: "", position)
                    }
                }
            }
        }
    }

    private fun createTagChip(context: Context, chipId: Int, chipName: String, slabIndex: Int): Chip {
        return Chip(context).apply {
            id = chipId
            setChipDrawable(ChipDrawable.createFromAttributes(context, null, 0, R.style.Widget_MaterialComponents_Chip_Choice))
            text = chipName
            setChipBackgroundColorResource(R.color.chip_background_state_colors)
            setTextColor(ContextCompat.getColor(context, R.color.chip_text_state_colors))
            setRippleColorResource(R.color.chip_ripple_color)
            isChecked = chipId == slabIndex
        }

    }
}