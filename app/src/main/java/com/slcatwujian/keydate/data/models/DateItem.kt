package com.slcatwujian.keydate.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 日期类型枚举
 */
enum class DateItemType {
    DATE,                // 日期页面的日期
    IMPORTANT_DATE,      // 重要日期页面的用户日期
    SYSTEM_HOLIDAY       // 系统节假日
}

/**
 * 地区枚举
 */
enum class DateRegion {
    CHINA,    // 中国
    GLOBAL    // 全球
}

/**
 * 日期项数据模型
 *
 * @property id 主键，自动生成
 * @property title 日期标题
 * @property content 日期内容描述
 * @property targetDate 目标日期时间戳（毫秒）
 * @property type 日期类型（用户创建/系统节假日）
 * @property region 地区（中国/全球），仅节假日使用
 * @property isReminderEnabled 是否启用提醒（当前版本未实现，保留字段）
 * @property createdAt 创建时间戳（毫秒）
 */
@Entity(tableName = "date_items")
data class DateItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val targetDate: Long,
    val type: DateItemType = DateItemType.DATE,
    val region: DateRegion? = null,
    val isReminderEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
