package com.mperezt.customcalendar.customCalendarView

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import es.redsys.ess.R
import es.redsys.ess.databinding.ItemCalendarDayBinding
import es.redsys.ess.presentation.model.CalendarDayPresentation
import es.redsys.ess.presentation.utils.constants.EXTRA_FRAGMENT_TAG
import es.redsys.ess.presentation.utils.constants.EXTRA_IS_FROM_CALENDAR
import es.redsys.ess.presentation.utils.constants.FRAGMENT_TAG_INCIDENT

class CalendarAdapterImpl(
    private val context: Context,
    private val selectable: Boolean = false,
    private val listener: OnDayClickListener? = null
) : BaseAdapter(), CalendarAdapter {

    private var days: List<CalendarDayPresentation> = emptyList()
    private var selectedPosition: Int = -1

    override fun setData(days: List<CalendarDayPresentation>) {
        this.days = days
        notifyDataSetChanged()
    }

    override fun refresh() {
        notifyDataSetChanged()
    }

    override fun getCount(): Int = days.size

    override fun getItem(position: Int): CalendarDayPresentation = days[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemCalendarDayBinding = if (convertView == null) {
            ItemCalendarDayBinding.inflate(LayoutInflater.from(context), parent, false)
        } else {
            ItemCalendarDayBinding.bind(convertView)
        }

        val day = getItem(position)
        configureDayView(binding, day, position)

        return binding.root
    }

    private fun configureDayView(binding: ItemCalendarDayBinding, day: CalendarDayPresentation, position: Int) {
        binding.itemDayTextView.apply {
            text = day.day
            textSize = 14f
            setTextColor(getDayTextColor(day, position == selectedPosition))
            background = getDayBackground(day, position == selectedPosition)
        }

        setClickListener(binding, day, position)
    }

    private fun getDayTextColor(day: CalendarDayPresentation, isSelected: Boolean): Int {
        return when {
            isSelected -> ContextCompat.getColor(context, R.color.white)
            day.enabled -> ContextCompat.getColor(context, android.R.color.black)
            else -> ContextCompat.getColor(context, R.color.light_grey)
        }
    }

    private fun getDayBackground(day: CalendarDayPresentation, isSelected: Boolean): Drawable? {
        return if (isSelected) {
            ContextCompat.getDrawable(context, R.drawable.background__selectable_day)
        } else if (day.selected) {
            ContextCompat.getDrawable(context, R.drawable.background__day_selected)
        } else {
            null
        }
    }

    private fun setClickListener(binding: ItemCalendarDayBinding, day: CalendarDayPresentation, position: Int) {
        binding.itemDayTextView.setOnClickListener {
            handleDayClick(day, position,binding)
            listener?.onDayClick(day)
        }
    }

    private fun handleDayClick(day: CalendarDayPresentation, position: Int, binding: ItemCalendarDayBinding) {
        if (selectable && day.enabled) {
            selectedPosition = position
            notifyDataSetChanged()
        }
        if (day.selected) {
            startDetailActivity(binding)
        }
    }

    private fun startDetailActivity(binding: ItemCalendarDayBinding) {
        val navController = binding.root.findNavController()
        val bundle = bundleOf(
            EXTRA_FRAGMENT_TAG to FRAGMENT_TAG_INCIDENT,
            EXTRA_IS_FROM_CALENDAR to true,
        )
        navController.navigate(R.id.action_global_state_fragment_to_detail_activity, bundle)
    }

    interface OnDayClickListener {
        fun onDayClick(day: CalendarDayPresentation)
    }
}