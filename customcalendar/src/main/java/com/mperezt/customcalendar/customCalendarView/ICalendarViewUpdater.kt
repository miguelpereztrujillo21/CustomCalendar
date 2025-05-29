package com.mperezt.customcalendar.customCalendarView

import com.mperezt.customcalendar.customCalendarView.adapter.CalendarAdapterImpl
import com.mperezt.customcalendar.model.CalendarDayPresentation

interface ICalendarViewUpdater {
    fun updateCalendar(
        selectedDays: Set<CalendarDayPresentation>,
        maxBackSteps: Int,
        enabledDays: Int? = null,
        selectable: Boolean? = false,
        listener: CalendarAdapterImpl.OnDayClickListener? = null
    )
}