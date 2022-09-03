package net.store.divineit.ui.main

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import net.store.divineit.R
import net.store.divineit.databinding.MultiplierListItemBinding
import net.store.divineit.models.MultiplierClass
import net.store.divineit.utils.AppConstants

class MultiplierListAdapter internal constructor(
    private val callback: (Int, String, Int, String, String?) -> Unit,
    private val sliderCallback: (String, Int, String?) -> Unit
) : RecyclerView.Adapter<MultiplierListAdapter.ViewHolder>() {

    private var baseModuleCode: String? = ""
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

    fun submitList(dataList: List<MultiplierClass>, baseModuleCode: String?) {
        this.baseModuleCode = baseModuleCode
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
                binding.customValueLayout.visibility = View.GONE
                binding.slider.visibility = View.VISIBLE
//                if (item.slabs.isEmpty()) {
//                    binding.linearSlider.visibility = View.GONE
//                    return
//                }

                val sliderMaxRange = 50.0

//                try {
//                    sliderMaxRange = item.slabs[0].toDouble()
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }

                binding.slider.valueTo = sliderMaxRange.toFloat()

                val sliderStepSize = 1 //item.slabConfig.increment ?: 1

                binding.slider.stepSize = sliderStepSize.toFloat()

                binding.slider.addOnChangeListener { slider, value, fromUser -> /* `value` is the argument you need */
                    sliderCallback(item.code ?: "", value.toInt(), baseModuleCode)
                }
            } else {
                binding.slider.visibility = View.GONE
                for ((index, slab) in item.slabs.withIndex()) {
                    val isNumber = slab.matches("((\\d+\\.?)*\\d*)".toRegex())
                    if (isNumber) {
                        if (item.slabConfig?.showRange == true) {
                            var prefix = ""
                            if (item.slabTexts.size > index) {
                                prefix = item.slabTexts[index]
                            }

                            val increment = 1
                            var startItem = increment

                            if (index > 0) {
                                try {
                                    val previousItem = item.slabs[index - 1].toDouble().toInt()
                                    startItem = previousItem + increment
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            try {
                                val slabPrice = slab.toDouble().toInt()
                                val slabText = if (prefix.isBlank()) {
                                    "$startItem-${slabPrice}"
                                } else {
                                    "$prefix($startItem-${slabPrice})"
                                }
                                binding.chipGroup.addView(createTagChip(mContext, index, slabText, item.slabIndex), index)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                binding.chipGroup.addView(createTagChip(mContext, index, slab, item.slabIndex), index)
                            }
                        } else {
                            var prefix = ""
                            if (item.slabTexts.size > index) {
                                prefix = item.slabTexts[index]
                            }

                            try {
                                val slabPrice = slab.toDouble().toInt()
                                val slabText = if (prefix.isBlank()) {
                                    "$slabPrice"
                                } else {
                                    "$prefix(${slabPrice})"
                                }
                                binding.chipGroup.addView(createTagChip(mContext, index, slabText, item.slabIndex), index)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                binding.chipGroup.addView(createTagChip(mContext, index, slab, item.slabIndex), index)
                            }
                        }
                    } else {
                        binding.chipGroup.addView(createTagChip(mContext, index, slab, item.slabIndex), index)
                    }
                }

                if (item.slabConfig?.customUser == true) {
                    binding.chipGroup.addView(createTagChip(mContext, item.slabs.size, AppConstants.labelCustom,
                        item.slabIndex), item.slabs.size)
                }

                binding.customValueLayout.visibility = if (item.slabs.isNotEmpty() && item.slabs.size == item.slabIndex) View.VISIBLE else View.GONE

                binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
                    if (checkedIds.size == 1) {
                        if (checkedIds[0] == item.slabs.size) {
                            binding.customValueLayout.visibility = View.VISIBLE
                        } else {
                            item.customValue = ""
                            binding.customValueLayout.visibility = View.GONE
                            callback(checkedIds[0], item.code ?: "", position, item.customValue ?: "", baseModuleCode)
                            notifyItemChanged(position)
                        }
                    }
                }

                binding.customValue.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        callback(item.slabs.size, item.code ?: "", position, item.customValue ?: "", baseModuleCode)
                        return@setOnEditorActionListener true
                    }
                    false
                }

//                val textWatcher = object : TextWatcher {
//                    override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
//                        val newValue = cs.toString()
//                        val oldValue = item.customValue
//                        if (oldValue == newValue || (oldValue.isNullOrBlank() && newValue.isBlank())) return
//                        callback(item.slabs.size, item.code ?: "", position, cs.toString(), baseModuleCode)
//                    }
//
//                    override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
//                    override fun afterTextChanged(arg0: Editable) {}
//                }
//
//                binding.customValue.removeTextChangedListener(textWatcher)
//
//                binding.customValue.setText(item.customValue)
//
//                binding.customValue.addTextChangedListener(textWatcher)
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