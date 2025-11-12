package com.slcatwujian.keydate.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 日期项数据模型
 *
 * @property id 主键，自动生成
 * @property title 日期标题
 * @property content 日期内容描述
 * @property targetDate 目标日期时间戳（毫秒）
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
    val isReminderEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
