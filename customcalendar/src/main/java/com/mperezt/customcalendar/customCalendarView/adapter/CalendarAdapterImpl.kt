package com.mperezt.customcalendar.customCalendarView.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import es.redsys.ess.R
import es.redsys.ess.databinding.ItemCalendarDayBinding
import es.redsys.ess.presentation.model.CalendarDayPresentation

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
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            includeFontPadding = false
            setPadding(0, 0, 0, 0)
        }

        setClickListener(binding, day, position)
    }

    private fun getDayTextColor(day: CalendarDayPresentation, isSelected: Boolean): Int {
        return when {
            isSelected || day.selected -> ContextCompat.getColor(context, R.color.white)
            day.enabled -> ContextCompat.getColor(context, android.R.color.black)
            else -> ContextCompat.getColor(context, R.color.neutral_light_darknest)
        }
    }

    private fun getDayBackground(day: CalendarDayPresentation, isSelected: Boolean): Drawable? {
        return if (isSelected || day.selected) {
            ContextCompat.getDrawable(context, R.drawable.background__day_selected)
        } else {
            null
        }
    }

    private fun setClickListener(binding: ItemCalendarDayBinding, day: CalendarDayPresentation, position: Int) {
        binding.itemDayTextView.setOnClickListener {
            handleDayClick(day, position)
        }
    }

    private fun handleDayClick(day: CalendarDayPresentation, position: Int) {
        if (day.enabled && day.selected) {
            selectedPosition = position
            notifyDataSetChanged()
            listener?.onDayClick(day)
        }
    }

    interface OnDayClickListener {
        fun onDayClick(day: CalendarDayPresentation)
    }
}