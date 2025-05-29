package com.mperezt.customcalendar.model

data class CalendarDayPresentation(
    val fullDate: String,
    var itemId: Any? = null,
    val selected: Boolean,
    val enabled: Boolean,
    val isCurrentMonth: Boolean,
    val associatedItem: Any? = null
) {
    val day: String
        get() = fullDate.split("-").last()
}