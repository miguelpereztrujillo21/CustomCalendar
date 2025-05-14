package es.redsys.ess.presentation.features.globalState.calendar.customCalendarView

import es.redsys.ess.presentation.features.globalState.calendar.adapter.CalendarAdapterImpl
import es.redsys.ess.presentation.model.IncidentPresentation

interface ICalendarViewUpdater {
    fun updateCalendar(
        selectedDays: Set<IncidentPresentation>,
        maxBackSteps: Int,
        enabledDays: Int? = null,
        selectable: Boolean? = false,
        listener: CalendarAdapterImpl.OnDayClickListener? = null
    )
}