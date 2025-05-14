package es.redsys.ess.presentation.features.globalState.calendar.customCalendarView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import es.redsys.ess.databinding.ViewCustomCalendarBinding
import es.redsys.ess.presentation.features.globalState.calendar.adapter.CalendarAdapterImpl
import es.redsys.ess.presentation.model.IncidentPresentation

class CustomCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    listener: CalendarAdapterImpl.OnDayClickListener? = null
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ViewCustomCalendarBinding =
        ViewCustomCalendarBinding.inflate(LayoutInflater.from(context), this, true)
    private val incidents = mutableSetOf<IncidentPresentation>()
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
        days: List<IncidentPresentation>,
        maxBackSteps: Int,
        enabledDays: Int? = null,
        selectable: Boolean? = false,
        listener: CalendarAdapterImpl.OnDayClickListener? = null
    ) {
        incidents.clear()
        incidents.addAll(days)
        (icalendarViewUpdater as CustomCalendarViewSetup).updateCalendar(incidents, maxBackSteps, enabledDays, selectable, listener)
    }
}