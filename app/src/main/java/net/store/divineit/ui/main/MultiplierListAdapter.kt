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

            if (item.slabConfig.inputType == "slider") {
                binding.linearSlider.visibility = View.VISIBLE
                if (item.slabs.isEmpty()) {
                    binding.linearSlider.visibility = View.GONE
                    return
                }

                val sliderMaxRange = when (val maxRange = item.slabs[0]) {
                    is Int -> {
                        maxRange.toInt()
                    }
                    is Double -> {
                        maxRange.toInt()
                    }
                    else -> {
                        10
                    }
                }

                binding.slider.valueTo = sliderMaxRange.toFloat()

                binding.maxText.text = "MAX ($sliderMaxRange)"

                val sliderStepSize = when (val stepSize = item.slabConfig.increment) {
                    is Int -> {
                        stepSize.toInt()
                    }
                    is Double -> {
                        stepSize.toInt()
                    }
                    else -> {
                        1
                    }
                }

                binding.slider.stepSize = sliderStepSize.toFloat()
            } else {
                binding.linearSlider.visibility = View.GONE
                for ((index, slab) in item.slabs.withIndex()) {
                    var prefix = ""
                    if (item.slabConfig.slabTexts.size > index) {
                        prefix = item.slabConfig.slabTexts[index].title ?: ""
                    }

                    val startRange = when (item.slabConfig.increment) {
                        is Int -> {
                            val increment = item.slabConfig.increment.toInt()
                            var startItem = increment
                            if (index > 0) {
                                val previousItem = when (val temp = item.slabs[index - 1]) {
                                    is Int -> {
                                        temp.toInt()
                                    }
                                    is Double -> {
                                        temp.toInt()
                                    }
                                    else -> {
                                        0
                                    }
                                }
                                startItem = previousItem + increment
                            }
                            startItem
                        }
                        is Double -> {
                            val increment = item.slabConfig.increment.toInt()
                            var startItem = increment
                            if (index > 0) {
                                val previousItem = when (val temp = item.slabs[index - 1]) {
                                    is Int -> {
                                        temp.toInt()
                                    }
                                    is Double -> {
                                        temp.toInt()
                                    }
                                    else -> {
                                        0
                                    }
                                }
                                startItem = previousItem + increment
                            }
                            startItem
                        }
                        else -> {
                            -1
                        }
                    }

                    val slabText = when (slab) {
                        is Int -> {
                            if (prefix.isBlank()) {
                                if (startRange == -1 || startRange == slab.toInt()) "${slab.toInt()}" else "$startRange-${slab.toInt()}"
                            } else {
                                if (startRange == -1 || startRange == slab.toInt()) "$prefix(${slab.toInt()})" else "$prefix($startRange-${slab.toInt()})"
                            }
                        }
                        is Double -> {
                            if (prefix.isBlank()) {
                                if (startRange == -1 || startRange == slab.toInt()) "${slab.toInt()}" else "$startRange-${slab.toInt()}"
                            } else {
                                if (startRange == -1 || startRange == slab.toInt()) "$prefix(${slab.toInt()})" else "$prefix($startRange-${slab.toInt()})"
                            }
                        }
                        else -> slab.toString()
                    }
                    binding.chipGroup.addView(createTagChip(mContext, index, slabText, item.slabIndex), index)
                }

                binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
                    if (checkedIds.size == 1) {
                        callback(checkedIds[0], item.code, position)
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