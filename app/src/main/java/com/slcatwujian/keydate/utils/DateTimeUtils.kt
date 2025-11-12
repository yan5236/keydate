package com.slcatwujian.keydate.utils

import android.content.Context
import com.slcatwujian.keydate.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

/**
 * 日期时间工具类
 * 提供日期格式化、时间差计算等功能
 */
object DateTimeUtils {

    /**
     * 时间差数据类
     * @property days 天数
     * @property hours 小时数
     * @property minutes 分钟数
     * @property seconds 秒数
     * @property isExpired 是否已过期
     */
    data class TimeDifference(
        val days: Long,
        val hours: Long,
        val minutes: Long,
        val seconds: Long,
        val isExpired: Boolean
    )

    /**
     * 计算两个时间戳之间的时间差
     * @param targetTime 目标时间戳（毫秒）
     * @param currentTime 当前时间戳（毫秒）
     * @return TimeDifference对象，包含天、时、分、秒以及是否过期
     */
    fun calculateTimeDifference(targetTime: Long, currentTime: Long): TimeDifference {
        val diffInMillis = targetTime - currentTime
        val isExpired = diffInMillis < 0
        val absoluteDiff = abs(diffInMillis)

        val seconds = (absoluteDiff / 1000) % 60
        val minutes = (absoluteDiff / (1000 * 60)) % 60
        val hours = (absoluteDiff / (1000 * 60 * 60)) % 24
        val days = absoluteDiff / (1000 * 60 * 60 * 24)

        return TimeDifference(days, hours, minutes, seconds, isExpired)
    }

    /**
     * 格式化时间差为可读字符串
     * @param context 上下文对象，用于获取本地化字符串
     * @param timeDiff 时间差对象
     * @return 格式化后的字符串，例如："还有3天5小时20分30秒"或"已过2天10小时15分5秒"
     */
    fun formatTimeDifference(context: Context, timeDiff: TimeDifference): String {
        return if (timeDiff.isExpired) {
            context.getString(
                R.string.time_expired_format,
                timeDiff.days,
                timeDiff.hours,
                timeDiff.minutes,
                timeDiff.seconds
            )
        } else {
            context.getString(
                R.string.time_remaining_format,
                timeDiff.days,
                timeDiff.hours,
                timeDiff.minutes,
                timeDiff.seconds
            )
        }
    }

    /**
     * 格式化日期为显示格式
     * @param timestamp 时间戳（毫秒）
     * @param locale 语言环境
     * @return 格式化后的日期字符串，例如："2025-03-15 14:30"
     */
    fun formatDate(timestamp: Long, locale: Locale = Locale.getDefault()): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", locale)
        return dateFormat.format(Date(timestamp))
    }

    /**
     * 格式化日期为完整格式（包含星期）
     * @param timestamp 时间戳（毫秒）
     * @param locale 语言环境
     * @return 格式化后的日期字符串，例如："2025年3月15日 星期六 14:30"
     */
    fun formatDateWithWeekday(timestamp: Long, locale: Locale = Locale.getDefault()): String {
        val dateFormat = if (locale.language == "zh") {
            SimpleDateFormat("yyyy年M月d日 EEEE HH:mm", locale)
        } else {
            SimpleDateFormat("EEEE, MMM d, yyyy HH:mm", locale)
        }
        return dateFormat.format(Date(timestamp))
    }

    /**
     * 从年月日时分创建时间戳
     * @param year 年
     * @param month 月（1-12）
     * @param day 日
     * @param hour 小时（0-23）
     * @param minute 分钟（0-59）
     * @return 时间戳（毫秒）
     */
    fun createTimestamp(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(year, month - 1, day, hour, minute, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
