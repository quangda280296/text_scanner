package thent.vietmobi.textscanner.utils

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    private var DATE_FORMAT = "dd/MM/yyyy"
    private const val DATE_TIME_FORMAT = "HH:mm dd/MM/yyyy"
    val DATE_FORMAT_UTC = "yyyy-MM-dd HH:mm:ss'UTC'"
    const val DATE_FORMAT_TIME = "HH:mm:ss dd-MM-yyyy"
    private var TIME_FORMAT = "HH:mm"

    @SuppressLint("SimpleDateFormat")
    fun formatLongToDateTime(timeLong: Long?, isUTC: Boolean): String {
        return if (isUTC) {
            formatUTCToLocalDateTime(timeLong)
        } else {
            SimpleDateFormat(DATE_FORMAT).format(Date(timeLong!!))
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateFormat(strTime: String): Date? {
        try {
            return SimpleDateFormat(DATE_FORMAT_TIME).parse(strTime)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    @SuppressLint("SimpleDateFormat")
    fun formatLongToDateTime(timeLong: Long?, isUTC: Boolean, format: String): String {
        return if (isUTC) {
            formatUTCToLocalDateTime(timeLong)
        } else {
            SimpleDateFormat(format).format(Date(timeLong!!))
        }
    }

    fun parseDateFormatTime(strDate: String): String {
        val format = SimpleDateFormat("HH:mm:ss")
        try {
            val date = format.parse(strDate)
            val dateFormat = SimpleDateFormat("HH:mm", Locale.US)
            return dateFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null.toString()
    }

    fun parseDateFormatUShowing(strDate: String): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            val date = format.parse(strDate)
            val dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US)
            return dateFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null.toString()
    }

    fun parseDateFormatProfile(strDate: String): String {
        val format = SimpleDateFormat("yyyy-MM-dd")
        try {
            val date = format.parse(strDate)
            val dateformat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            return dateformat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null.toString()
    }


    @SuppressLint("SimpleDateFormat")
    fun formatLongToTime(timeLong: Long?): String {
        val oldFormatter = SimpleDateFormat(DATE_TIME_FORMAT)
        return oldFormatter.format(timeLong)
    }

    @SuppressLint("SimpleDateFormat")
    fun formatUTCToLocalDateTime(timeLong: Long?): String {
        val value = convertUTCToLocalTime(timeLong!!)
        val oldFormatter = SimpleDateFormat(DATE_TIME_FORMAT)
        return oldFormatter.format(value)
    }

    @SuppressLint("SimpleDateFormat")
    fun formatTimeUTCToLocalTime(time: String): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        var today = sdf.format(Date())
        today += " $time"

        val df = SimpleDateFormat(DATE_TIME_FORMAT)
        df.timeZone = TimeZone.getTimeZone("UTC")
        var date: Date? = null
        try {
            date = df.parse(today)
            val dfNew = SimpleDateFormat(TIME_FORMAT)
            dfNew.timeZone = TimeZone.getDefault()
            return dfNew.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            return time
        }

    }

    @SuppressLint("SimpleDateFormat")
    fun formatLocalTimeToTimeStamp(dateString: String): Long {
        var startDate: Long = 0
        try {
            val sdf = SimpleDateFormat(DATE_FORMAT)
            val date = sdf.parse(dateString)
            startDate = date.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return startDate
    }

    fun convertLocalTimeToUTC(timestampMs: Long): Long {
        val localZone = TimeZone.getDefault()
        val offset = localZone.getOffset(timestampMs).toLong()
        return timestampMs - offset
    }

    fun convertUTCToLocalTime(timestampMs: Long): Long {
        val localZone = TimeZone.getDefault()
        val offset = localZone.getOffset(timestampMs).toLong()
        return timestampMs + offset
    }
}