package com.slcatwujian.keydate.data.database

import androidx.room.TypeConverter
import com.slcatwujian.keydate.data.models.DateItemType
import com.slcatwujian.keydate.data.models.DateRegion

/**
 * Room 数据库类型转换器
 * 用于将枚举类型转换为数据库可存储的基本类型
 */
class Converters {

    /**
     * 将 DateItemType 枚举转换为字符串存储
     */
    @TypeConverter
    fun fromDateItemType(type: DateItemType): String {
        return type.name
    }

    /**
     * 将字符串转换为 DateItemType 枚举
     */
    @TypeConverter
    fun toDateItemType(value: String): DateItemType {
        return DateItemType.valueOf(value)
    }

    /**
     * 将 DateRegion 枚举转换为字符串存储（可为 null）
     */
    @TypeConverter
    fun fromDateRegion(region: DateRegion?): String? {
        return region?.name
    }

    /**
     * 将字符串转换为 DateRegion 枚举（可为 null）
     */
    @TypeConverter
    fun toDateRegion(value: String?): DateRegion? {
        return value?.let { DateRegion.valueOf(it) }
    }
}
