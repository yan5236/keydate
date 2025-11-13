package com.slcatwujian.keydate.utils

import android.content.Context
import com.slcatwujian.keydate.R

/**
 * 智能倒计时格式化工具
 * 实现天→时→分→秒递进显示逻辑
 */
object CountdownFormatter {

    /**
     * 倒计时数据类
     */
    data class CountdownData(
        val value: Long,           // 数值
        val unit: TimeUnit,        // 单位
        val isExpired: Boolean     // 是否已过期
    )

    /**
     * 时间单位枚举
     */
    enum class TimeUnit {
        DAYS,    // 天
        HOURS,   // 小时
        MINUTES, // 分钟
        SECONDS  // 秒
    }

    /**
     * 格式化倒计时（智能选择单位）
     * 规则:
     * - ≥ 1天: 只显示天数
     * - < 1天 且 ≥ 1小时: 只显示小时
     * - < 1小时 且 ≥ 1分钟: 只显示分钟
     * - < 1分钟: 只显示秒数
     *
     * @param targetTime 目标时间戳（毫秒）
     * @param currentTime 当前时间戳（毫秒）
     * @return 倒计时数据
     */
    fun formatSmart(targetTime: Long, currentTime: Long): CountdownData {
        val diffMillis = targetTime - currentTime
        val isExpired = diffMillis < 0
        val absDiffMillis = kotlin.math.abs(diffMillis)

        // 计算各单位的值
        val seconds = absDiffMillis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days >= 1 -> CountdownData(days, TimeUnit.DAYS, isExpired)
            hours >= 1 -> CountdownData(hours, TimeUnit.HOURS, isExpired)
            minutes >= 1 -> CountdownData(minutes, TimeUnit.MINUTES, isExpired)
            else -> CountdownData(seconds, TimeUnit.SECONDS, isExpired)
        }
    }

    /**
     * 将倒计时数据转换为显示文本
     *
     * @param context Android上下文
     * @param data 倒计时数据
     * @return 格式化后的文本,例如: "还有 3 天" 或 "已过 2 小时"
     */
    fun formatToText(context: Context, data: CountdownData): String {
        val unitString = when (data.unit) {
            TimeUnit.DAYS -> context.getString(R.string.time_unit_days)
            TimeUnit.HOURS -> context.getString(R.string.time_unit_hours)
            TimeUnit.MINUTES -> context.getString(R.string.time_unit_minutes)
            TimeUnit.SECONDS -> context.getString(R.string.time_unit_seconds)
        }

        return if (data.isExpired) {
            context.getString(R.string.countdown_expired, data.value, unitString)
        } else {
            context.getString(R.string.countdown_remaining, data.value, unitString)
        }
    }

    /**
     * 一次性格式化（直接从时间戳到文本）
     *
     * @param context Android上下文
     * @param targetTime 目标时间戳（毫秒）
     * @param currentTime 当前时间戳（毫秒）
     * @return 格式化后的文本
     */
    fun format(context: Context, targetTime: Long, currentTime: Long): String {
        val data = formatSmart(targetTime, currentTime)
        return formatToText(context, data)
    }

    /**
     * 获取倒计时的数值部分（用于大号显示）
     *
     * @param targetTime 目标时间戳（毫秒）
     * @param currentTime 当前时间戳（毫秒）
     * @return 数值字符串
     */
    fun getValueString(targetTime: Long, currentTime: Long): String {
        val data = formatSmart(targetTime, currentTime)
        return data.value.toString()
    }

    /**
     * 获取倒计时的单位文本（用于大号显示）
     *
     * @param context Android上下文
     * @param targetTime 目标时间戳（毫秒）
     * @param currentTime 当前时间戳（毫秒）
     * @return 单位字符串
     */
    fun getUnitString(context: Context, targetTime: Long, currentTime: Long): String {
        val data = formatSmart(targetTime, currentTime)
        return when (data.unit) {
            TimeUnit.DAYS -> context.getString(R.string.time_unit_days)
            TimeUnit.HOURS -> context.getString(R.string.time_unit_hours)
            TimeUnit.MINUTES -> context.getString(R.string.time_unit_minutes)
            TimeUnit.SECONDS -> context.getString(R.string.time_unit_seconds)
        }
    }

    /**
     * 判断是否已过期
     */
    fun isExpired(targetTime: Long, currentTime: Long): Boolean {
        return targetTime < currentTime
    }
}
