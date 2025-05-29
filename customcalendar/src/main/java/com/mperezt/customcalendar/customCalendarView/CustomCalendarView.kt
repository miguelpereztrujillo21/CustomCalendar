package com.mperezt.customcalendar.customCalendarView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.mperezt.customcalendar.databinding.ViewCustomCalendarBinding
import com.mperezt.customcalendar.customCalendarView.adapter.CalendarAdapterImpl
import com.mperezt.customcalendar.model.CalendarDayPresentation

class CustomCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    listener: CalendarAdapterImpl.OnDayClickListener? = null
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ViewCustomCalendarBinding =
        ViewCustomCalendarBinding.inflate(LayoutInflater.from(context), this, true)
    private val selectedDays = mutableListOf<CalendarDayPresentation>()
    private val icalendarViewUpdater: ICalendarViewUpdater = CustomCalendarViewSetup(
        context, binding, listener
    )

    init {
        setupCalendar()
    }

    private fun setupCalendar() {
        (icalendarViewUpdater as CustomCalendarViewSetup).setupCalendar()
    }

    fun setSelectedDays(
        days: List<CalendarDayPresentation>,
        maxBackSteps: Int,
        enabledDays: Int? = null,
        selectable: Boolean = false,
        listener: CalendarAdapterImpl.OnDayClickListener? = null
    ) {
        selectedDays.clear()
        selectedDays.addAll(days)
        (icalendarViewUpdater as CustomCalendarViewSetup).updateCalendar(
            selectedDays, maxBackSteps, enabledDays, selectable, listener
        )
    }
}