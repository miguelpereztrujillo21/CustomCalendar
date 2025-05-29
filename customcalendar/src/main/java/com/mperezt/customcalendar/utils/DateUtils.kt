package com.mperezt.customcalendar.utils

import android.util.Log
import com.mperezt.customcalendar.utils.constants.COMPLETE_DATE_FORMAT
import com.mperezt.customcalendar.utils.constants.COMPLETE_DATE_TIME_FORMAT
import com.mperezt.customcalendar.utils.constants.SIMPLE_DATE_FORMAT
import es.redsys.ess.presentation.model.ServicePresentation
import es.redsys.ess.presentation.utils.constants.EMPTY_STRING
import es.redsys.ess.utils.constants.COMPLETE_DATE_FORMAT
import es.redsys.ess.utils.constants.COMPLETE_DATE_TIME_FORMAT
import es.redsys.ess.utils.constants.COMPLETE_SLASH_DATE_TIME_FORMAT
import es.redsys.ess.utils.constants.LONG_DATE_FORMAT
import es.redsys.ess.utils.constants.LONG_DATE_WEED_DAY
import es.redsys.ess.utils.constants.MONTH_FORMAT
import es.redsys.ess.utils.constants.MONTH_YEAR_FORMAT
import es.redsys.ess.utils.constants.SIMPLE_DATE_FORMAT
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.Date
import java.util.Locale


object DateUtils {

    private val dateFormatter = DateTimeFormatter.ofPattern(COMPLETE_DATE_FORMAT)
    private val dateTimeFormatter = DateTimeFormatter.ofPattern(COMPLETE_DATE_TIME_FORMAT)

    fun isDayWithinRange(day: LocalDate, startDate: LocalDate, endDate: LocalDate): Boolean {
        return !day.isBefore(startDate) && !day.isAfter(endDate)
    }

    fun findIncidentForDay(dayDate: LocalDate, incidents: Set<ServicePresentation>): ServicePresentation? {
        return incidents.find { incident ->
            try {
                val incidentDateTime: LocalDateTime = LocalDateTime.parse(incident.beginDate, dateTimeFormatter)
                val incidentDate: LocalDate = incidentDateTime.toLocalDate()
                incidentDate == dayDate
            } catch (e: DateTimeParseException) {
                Log.e("IncidentError", "Error parsing incident date: ${incident.beginDate}. Exception: ${e.message}", e)
                false
            }
        }
    }

    fun formatDate(date: Any): String {
        return when (date) {
            is LocalDate -> date.format(dateFormatter)
            is Date -> SimpleDateFormat(SIMPLE_DATE_FORMAT, Locale.getDefault()).format(date)
            is String -> runCatching {
                val parsedDate = SimpleDateFormat(COMPLETE_DATE_FORMAT, Locale.getDefault()).parse(date) ?: Date()
                SimpleDateFormat(SIMPLE_DATE_FORMAT, Locale.getDefault()).format(parsedDate)
            }.getOrDefault(" ")
            else -> throw IllegalArgumentException("Unsupported date type")
        }
    }

    fun getMonthAbbreviation(monthNumber: String): String {
        val month = monthNumber.toIntOrNull() ?: return " "
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month - 1)
        val dateFormat = SimpleDateFormat(MONTH_FORMAT
            , Locale.getDefault())
        return dateFormat.format(calendar.time)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    fun getCurrentMonthYearTitle(monthNumber: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, monthNumber)
        val dateFormat = SimpleDateFormat(MONTH_YEAR_FORMAT, Locale.getDefault())
        return dateFormat.format(calendar.time)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    fun formatDurationFromString(durationString: String): String {
        // "00080000" (HHMMSS00)
        if (durationString.length < 6) return "0h 0m"

        try {
            val hours = durationString.substring(0, 4).toIntOrNull() ?: 0
            val minutes = durationString.substring(4, 6).toIntOrNull() ?: 0

            return "${hours}h ${minutes}m"
        } catch (e: Exception) {
            Log.e("DateUtils", "Error format duration: ${e.message}")
            return "0h 0m"
        }
    }

    fun formatDateToLongFormat(dateString: String): String {
        val inputFormat = SimpleDateFormat(COMPLETE_DATE_TIME_FORMAT, Locale.getDefault())
        val outputFormat = SimpleDateFormat(LONG_DATE_FORMAT, Locale("es", "ES"))

        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: "")
        } catch (e: Exception) {
            "Invalid format"
        }
    }

    fun formatToLongDate(dateString: String): String {
        val inputFormat = SimpleDateFormat(COMPLETE_DATE_TIME_FORMAT, Locale("es", "ES"))
        val outputFormat = SimpleDateFormat(LONG_DATE_WEED_DAY, Locale("es", "ES"))

        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: "")
        } catch (e: Exception) {
            "Formato inválido"
        }
    }

    fun formatToHoursAndMinutes(durationString: String): String {
        if (durationString.length < 6) return "0 horas, 0 minutos"

        return try {
            val hours = durationString.substring(0, 4).toIntOrNull() ?: 0
            val minutes = durationString.substring(4, 6).toIntOrNull() ?: 0

            val hourText = if (hours == 1) "hora" else "horas"
            val minuteText = if (minutes == 1) "minuto" else "minutos"

            "$hours $hourText, $minutes $minuteText"
        } catch (e: Exception) {
            Log.e("DateUtils", "Error al formatear duración: ${e.message}")
            "0 horas, 0 minutos"
        }
    }

    fun convertToSlashFormat(dashDate: String): String {
        return try {
            LocalDateTime.parse(dashDate, DateTimeFormatter.ofPattern(COMPLETE_DATE_TIME_FORMAT))
                .format(DateTimeFormatter.ofPattern(COMPLETE_SLASH_DATE_TIME_FORMAT))
        } catch (e: Exception) {
            dashDate.replace("-", "/")
        }
    }

    fun extractTimeFromDateTime(dateTimeString: String): String {
        //  "16-04-2025 07:00:00"
        return try {
            dateTimeString.substringAfter(" ").substringBeforeLast(":")
        } catch (e: Exception) {
            e.message?.let { Log.e("DateUtils", it) }
            ""
        }
    }
    fun normalizeDate(date: String, sourceFormat: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern(sourceFormat)
        val outputFormatter = DateTimeFormatter.ofPattern(COMPLETE_DATE_TIME_FORMAT)

        return try {
            LocalDateTime.parse(date, inputFormatter).format(outputFormatter)
        } catch (e: Exception) {
            try {
                val localDate = LocalDate.parse(date, inputFormatter)
                localDate.atStartOfDay().format(outputFormatter)
            } catch (e: Exception) {
                Log.e("DateUtils", "Error al normalizar fecha: ${e.message}")
                date
            }
        }
    }
}