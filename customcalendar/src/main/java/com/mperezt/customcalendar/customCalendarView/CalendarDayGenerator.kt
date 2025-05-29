package com.mperezt.customcalendar.customCalendarView

import com.mperezt.customcalendar.model.CalendarDayPresentation
import com.mperezt.customcalendar.utils.constants.COMPLETE_DATE_FORMAT
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object CalendarDayGenerator {

    private val dateFormatter = DateTimeFormatter.ofPattern(COMPLETE_DATE_FORMAT)

    fun generateCalendarDays(
        selectedDays: List<CalendarDayPresentation>,
        monthOffset: Int = 0,
        enabledDays: Int? = null
    ): List<CalendarDayPresentation> {
        val today = LocalDate.now()
        val firstDayOfMonth = today.plusMonths(monthOffset.toLong()).withDayOfMonth(1)
        val startEnabledDate = if (enabledDays != null) today.minusDays((enabledDays - 1).toLong()) else LocalDate.MIN
        val daysInMonth = firstDayOfMonth.lengthOfMonth()

        val previousMonthDays = generateLastMonthWeekWhiteSpacesDays(firstDayOfMonth)
        val currentMonthDays = generateCurrentMonthDays(
            firstDayOfMonth, daysInMonth, selectedDays, startEnabledDate, today
        )

        return previousMonthDays + currentMonthDays
    }

    private fun generateLastMonthWeekWhiteSpacesDays(firstDayOfMonth: LocalDate): List<CalendarDayPresentation> {
        val dayOfWeekValue = (firstDayOfMonth.dayOfWeek.value % 7)
        val adjustedDayOfWeekValue = if (dayOfWeekValue == 0) 6 else dayOfWeekValue - 1
        return List(adjustedDayOfWeekValue) {
            CalendarDayPresentation("", null, false, false, false)
        }
    }

    private fun generateCurrentMonthDays(
        firstDayOfMonth: LocalDate,
        daysInMonth: Int,
        selectedDays: List<CalendarDayPresentation>,
        startEnabledDate: LocalDate,
        endEnabledDate: LocalDate
    ): List<CalendarDayPresentation> {
        return (1..daysInMonth).map { day ->
            val dayDate = firstDayOfMonth.withDayOfMonth(day)
            val fullDateString = dayDate.format(dateFormatter)
            val isEnabled = isDayWithinRange(dayDate, startEnabledDate, endEnabledDate)

            val selectedDay = findSelectedDayForDate(fullDateString, selectedDays)

            CalendarDayPresentation(
                fullDate = fullDateString,
                itemId = selectedDay?.itemId,
                selected = isEnabled && selectedDay != null,
                enabled = isEnabled,
                isCurrentMonth = true,
                associatedItem = selectedDay?.associatedItem
            )
        }
    }

    private fun findSelectedDayForDate(
        dateString: String,
        selectedDays: List<CalendarDayPresentation>
    ): CalendarDayPresentation? {
        return selectedDays.find { it.fullDate == dateString }
    }

    private fun isDayWithinRange(day: LocalDate, startDate: LocalDate, endDate: LocalDate): Boolean {
        return !day.isBefore(startDate) && !day.isAfter(endDate)
    }
}