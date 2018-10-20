package com.alexeymerov.weatherfm.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Get formatted string from date
 **/

enum class DateStringFormat(val stringFormat: String) {
    DD_MM_YYYY_SLASHED("dd/MM/yyyy")
}

fun Date.getString(stringFormat: DateStringFormat, isServerTime: Boolean = false): String {
    val simpleDateFormat = SimpleDateFormat(stringFormat.stringFormat, Locale.getDefault())
    if (isServerTime) simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT")
    return simpleDateFormat.format(this)
}

fun Date.getHourMinuteString(): String = getFormattedString("HH:mm")

fun Date.getPrettyDateString(): String = getFormattedString("dd.MM.yy")

fun Date.getYearString(): String = getFormattedString("yyyy")

fun Date.getDayMonthString(): String = getFormattedString("dd MMMM")
fun Date.getDayMonthShortString(): String = getFormattedString("dd MMM")

private fun Date.getFormattedString(pattern: String) = SimpleDateFormat(pattern, Locale.getDefault()).format(this)

/**
 * Get day, month or year from millis as int
 **/

enum class FieldType(val value: Int) {
    DAY(Calendar.DAY_OF_MONTH), MONTH(Calendar.MONTH), YEAR(Calendar.YEAR)
}

fun Long.getFieldInt(fieldType: FieldType): Int = Date(this).getFieldInt(fieldType)

fun Date.getFieldInt(fieldType: FieldType): Int = Calendar.getInstance().let {
    it.time = this
    it.get(fieldType.value)
}

/**
 * Calculate difference between dates
 **/

fun differenceBetweenInSeconds(start: Date, end: Date): Int {
    val duration = end.time - start.time
    return TimeUnit.MILLISECONDS.toSeconds(duration).toInt()
}

fun differenceBetweenInMinutes(start: Date, end: Date): Int {
    val duration = end.time - start.time
    return TimeUnit.MILLISECONDS.toMinutes(duration).toInt()
}

fun differenceBetweenInHours(start: Date, end: Date): Int {
    val duration = end.time - start.time
    return TimeUnit.MILLISECONDS.toHours(duration).toInt()
}

fun differenceBetweenInDays(start: Date, end: Date): Int {
    val duration = end.time - start.time
    return TimeUnit.MILLISECONDS.toDays(duration).toInt()
}

fun differenceBetweenInMonths(start: Date, end: Date): Int {
    val duration = end.time - start.time
    val days = TimeUnit.MILLISECONDS.toDays(duration).toInt()
    return when {
        days > 30 -> days / 30
        else -> 0
    }
}