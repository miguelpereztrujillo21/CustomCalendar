package com.mperezt.customcalendar.customCalendarView.adapter

import com.mperezt.customcalendar.model.CalendarDayPresentation

interface CalendarAdapter {
    fun setData(days: List<CalendarDayPresentation>)
    fun refresh()
}