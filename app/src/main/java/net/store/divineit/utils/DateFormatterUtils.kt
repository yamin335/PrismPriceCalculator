package net.store.divineit.utils

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

enum class DateFormat(val value: String) {
    format1(DateFormatterUtils.DATE_FORMAT_1),
    format2(DateFormatterUtils.DATE_FORMAT_2)
}

object DateFormatterUtils {
    const val DATE_FORMAT_1 = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val DATE_FORMAT_2 = "dd-MM-yyyy HH:mm:ss"

    @SuppressLint("SimpleDateFormat")
    fun formatDateFromString(timeStr: String, dateFormat: DateFormat): Date? {
        return try {
            SimpleDateFormat(dateFormat.value).parse(timeStr)
        } catch (e: ParseException) {
            null
        }

    }

    @SuppressLint("SimpleDateFormat")
    fun formatDateToString(date: Date, dateFormat: DateFormat): String? {
        return try {
            SimpleDateFormat(dateFormat.value).format(date)
        } catch (e: ParseException) {
            null
        }
    }
}