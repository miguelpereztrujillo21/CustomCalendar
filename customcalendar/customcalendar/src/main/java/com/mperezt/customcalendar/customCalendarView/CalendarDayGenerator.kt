package es.redsys.ess.presentation.features.globalState.calendar.customCalendarView

import es.redsys.ess.presentation.model.CalendarDayPresentation
import es.redsys.ess.presentation.model.IncidentPresentation
import es.redsys.ess.presentation.utils.DateUtils
import es.redsys.ess.presentation.utils.constants.SPACE
import es.redsys.ess.utils.constants.COMPLETE_DATE_FORMAT
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

object CalendarDayGenerator {

    fun generateCalendarDays(
        incidents: Set<IncidentPresentation>,
        monthOffset: Int = 0,
        enabledDays: Int? = null
    ): List<CalendarDayPresentation> {
        val today = LocalDate.now()
        val firstDayOfMonth = today.plusMonths(monthOffset.toLong()).withDayOfMonth(1)
        val startEnabledDate = if (enabledDays != null) today.minusDays((enabledDays - 1).toLong()) else LocalDate.MIN
        val daysInMonth = firstDayOfMonth.lengthOfMonth()

        val previousMonthDays = generateLastMonthWeekWhiteSpacesDays(firstDayOfMonth)
        val currentMonthDays = generateCurrentMonthDays(
            firstDayOfMonth, daysInMonth, incidents, startEnabledDate, today
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
        incidents: Set<IncidentPresentation>,
        startEnabledDate: LocalDate,
        endEnabledDate: LocalDate
    ): List<CalendarDayPresentation> {
        return (1..daysInMonth).map { day ->
            val dayDate = firstDayOfMonth.withDayOfMonth(day)
            val isEnabled = DateUtils.isDayWithinRange(dayDate, startEnabledDate, endEnabledDate)
            val incident = DateUtils.findIncidentForDay(dayDate, incidents)
            CalendarDayPresentation(
                fullDate = dayDate.toString(),
                isCurrentMonth = true,
                enabled = isEnabled,
                selected = isEnabled && incident != null,
                incidentId = incident?.incidentId
            )
        }
    }

    private fun findIncidentForDay(
        day: Int,
        calendar: Calendar,
        incidents: Set<IncidentPresentation>
    ): IncidentPresentation? {
        return incidents.find { incident ->
            val incidentDate = getDateFormat().parse(incident.startDate.split(SPACE)[0])
            val incidentCalendar = Calendar.getInstance().apply {
                if (incidentDate != null) {
                    time = incidentDate
                }
            }
            incidentCalendar.get(Calendar.DAY_OF_MONTH) == day &&
                    incidentCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                    incidentCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
        }
    }

    private fun getDateFormat(): SimpleDateFormat {
        return SimpleDateFormat(COMPLETE_DATE_FORMAT, Locale.getDefault())
    }
}