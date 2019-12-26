package com.example.imdb.extension
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun Date.toDateWithMonthConverter(): String {
    val DATE_FORMAT_WITH_MONTH = "MMMM dd, yyyy"
    val toDateWithMonth = SimpleDateFormat(DATE_FORMAT_WITH_MONTH, Locale.getDefault())
    return toDateWithMonth.format(this)
}

fun String.toDateConverter(): Date? {
    val FULL_DATE_FORMAT = "yyyy-MM-dd"
    val dateFormat = SimpleDateFormat(FULL_DATE_FORMAT, Locale.getDefault())
    var parsedDate: Date? = null
    try {
        parsedDate = dateFormat.parse(this)
    } catch (ignored: ParseException) {
        ignored.printStackTrace()
    }
    return parsedDate
}

fun Date.toDateWithYear(): String {
    val DATE_FORMAT_WITH_YEAR = "yyyy"
    val toDateWithYear = SimpleDateFormat(DATE_FORMAT_WITH_YEAR, Locale.getDefault())
    return toDateWithYear.format(this)
}