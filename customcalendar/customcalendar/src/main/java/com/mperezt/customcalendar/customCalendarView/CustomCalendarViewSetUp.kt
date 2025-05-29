package es.redsys.ess.presentation.features.globalState.calendar.customCalendarView

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import es.redsys.ess.R
import es.redsys.ess.databinding.ViewCustomCalendarBinding
import es.redsys.ess.presentation.features.globalState.calendar.adapter.CalendarAdapterImpl
import es.redsys.ess.presentation.model.CalendarDayPresentation
import es.redsys.ess.presentation.model.IncidentPresentation
import es.redsys.ess.presentation.utils.constants.SPACE
import es.redsys.ess.presentation.utils.constants.WEEK_DAYS
import es.redsys.ess.utils.constants.COMPLETE_DATE_FORMAT
import es.redsys.ess.utils.constants.MONTH_YEAR_FORMAT
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.Locale

class CustomCalendarViewSetup(
    private val context: Context,
    private val binding: ViewCustomCalendarBinding,
    private var listener: CalendarAdapterImpl.OnDayClickListener? = null
) : ICalendarViewUpdater {

    private var monthOffset = 0
    private var selectedDays: Set<IncidentPresentation> = emptySet()
    private var maxBackSteps: Int = 0
    private var enabledDays: Int? = null
    private var selectable: Boolean = false
    private var calendarDays: List<CalendarDayPresentation> = emptyList()

    fun setupCalendar() {
        binding.textViewTitle.text = getCurrentMonthTitle()
        addWeekDaysHeader()
        setupCalendarAdapter()
        setupNavigationButtons()
    }

    private fun getCurrentMonthTitle(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, monthOffset)
        val dateFormat = SimpleDateFormat(MONTH_YEAR_FORMAT, Locale.getDefault())
        return dateFormat.format(calendar.time)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    private fun setupCalendarAdapter() {
        calendarDays = CalendarDayGenerator.generateCalendarDays(selectedDays, monthOffset, enabledDays)
        binding.gridViewCalendarDays.adapter = CalendarAdapterImpl(context, selectable, listener).apply {
            setData(calendarDays)
        }
    }

    private fun addWeekDaysHeader() {
        binding.calendarHeaderDays.removeAllViews()
        val heightInDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            40f,
            context.resources.displayMetrics
        ).toInt()

        WEEK_DAYS.forEach { day ->
            val dayTextView = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(heightInDp, heightInDp, 1f)
                text = day
                textSize = 15f
                setTextColor(context.getColor(R.color.light_grey))
                gravity = Gravity.CENTER
            }
            binding.calendarHeaderDays.addView(dayTextView)
        }
    }

    private fun setupNavigationButtons() {
        updateNavigationButtonsState()
        binding.viewCalendarArrowNext.setOnClickListener {
            if (monthOffset < 0) {
                monthOffset++
                updateCalendar(selectedDays, maxBackSteps, enabledDays, selectable)
                updateNavigationButtonsState()
            }
        }

        binding.viewCalendarArrowBack.setOnClickListener {
            if (canNavigateBack()) {
                monthOffset--
                updateCalendar(selectedDays, maxBackSteps, enabledDays, selectable)
                updateNavigationButtonsState()
            }
        }
    }

    private fun canNavigateBack(): Boolean {
        return monthOffset > -maxBackSteps && hasEnabledDaysInPreviousMonth(enabledDays)
    }

    private fun hasEnabledDaysInPreviousMonth(daysToCheck: Int?): Boolean {
        val today = LocalDate.now()
        val dateFormatter = DateTimeFormatter.ofPattern(COMPLETE_DATE_FORMAT)
        val startOfCurrentMonth = today.withDayOfMonth(1)
        val startOfPreviousMonth = startOfCurrentMonth.minusMonths(1)

        if (daysToCheck == null) {
            return true
        }

        val isIncidentInPreviousMonth = calendarDays.any {
            val dateString = it.day.split(SPACE)[0]
            try {
                if (dateString.isNotEmpty()) {
                    val incidentDate = LocalDate.parse(dateString, dateFormatter)
                    it.enabled && incidentDate.isBefore(startOfCurrentMonth) && incidentDate.isAfter(startOfPreviousMonth.minusDays(1))
                } else {
                    false
                }
            } catch (e: DateTimeParseException) {
                false
            }
        }
        val isOverflowNegative = today.minusDays(daysToCheck.toLong()).isBefore(startOfCurrentMonth)

        return isIncidentInPreviousMonth || isOverflowNegative
    }

    private fun updateNavigationButtonsState() {
        binding.viewCalendarArrowNext.isSelected = monthOffset < 0
        binding.viewCalendarArrowBack.isSelected = canNavigateBack()
    }

    override fun updateCalendar(
        selectedDays: Set<IncidentPresentation>,
        maxBackSteps: Int,
        enabledDays: Int?,
        selectable: Boolean?,
        listener: CalendarAdapterImpl.OnDayClickListener?
    ) {
        this.selectedDays = selectedDays
        this.maxBackSteps = maxBackSteps
        this.enabledDays = enabledDays
        this.selectable = selectable ?: false
        this.listener = listener
        setupCalendarAdapter()
        binding.textViewTitle.text = getCurrentMonthTitle()
        updateNavigationButtonsState()
    }
}