package com.slcatwujian.keydate.data.models

import com.google.gson.annotations.SerializedName

/**
 * 节假日 JSON 数据模型
 *
 * JSON 格式示例:
 * {
 *   "year": 2025,
 *   "region": "china",
 *   "holidays": [
 *     {
 *       "id": "new_year",
 *       "name_cn": "元旦",
 *       "name_en": "New Year's Day",
 *       "date": "2025-01-01"
 *     }
 *   ]
 * }
 */
data class HolidayData(
    @SerializedName("year")
    val year: Int,

    @SerializedName("region")
    val region: String,  // "china" 或 "western"

    @SerializedName("holidays")
    val holidays: List<HolidayItem>  // 节假日列表
)

/**
 * 单个节假日项
 */
data class HolidayItem(
    @SerializedName("id")
    val id: String,  // 节假日唯一标识

    @SerializedName("name_cn")
    val nameCn: String,  // 中文名称

    @SerializedName("name_en")
    val nameEn: String,  // 英文名称

    @SerializedName("date")
    val date: String  // 日期 (yyyy-MM-dd)
)
