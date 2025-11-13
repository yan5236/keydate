package com.slcatwujian.keydate.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.slcatwujian.keydate.data.models.DateItem
import com.slcatwujian.keydate.data.models.DateItemType
import com.slcatwujian.keydate.data.models.DateRegion
import com.slcatwujian.keydate.data.models.HolidayData
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 节假日 JSON 解析器
 * 负责解析节假日 JSON 文件并转换为 DateItem 对象
 */
class HolidayParser(private val context: Context) {

    private val gson = Gson()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /**
     * 解析结果封装类
     */
    sealed class ParseResult {
        data class Success(val dateItems: List<DateItem>) : ParseResult()
        data class Error(val message: String) : ParseResult()
    }

    /**
     * 从 JSON 字符串解析节假日数据
     *
     * @param jsonString JSON 字符串内容
     * @param selectedRegion 用户选择的地区（CHINA 或 GLOBAL）
     * @return 解析结果
     */
    fun parseHolidayJson(jsonString: String, selectedRegion: DateRegion): ParseResult {
        return try {
            // 解析 JSON
            val holidayData = gson.fromJson(jsonString, HolidayData::class.java)
                ?: return ParseResult.Error("JSON 格式错误：无法解析")

            // 验证必要字段
            if (holidayData.year <= 0) {
                return ParseResult.Error("年份无效：${holidayData.year}")
            }

            if (holidayData.holidays.isEmpty()) {
                return ParseResult.Error("节假日列表为空")
            }

            // 验证地区匹配
            val jsonRegion = when (holidayData.region.lowercase()) {
                "china" -> DateRegion.CHINA
                "western", "global" -> DateRegion.GLOBAL
                else -> return ParseResult.Error("不支持的地区：${holidayData.region}")
            }

            if (jsonRegion != selectedRegion) {
                return ParseResult.Error("JSON 文件地区(${holidayData.region})与选择的地区不匹配")
            }

            // 获取当前语言设置
            val currentLanguage = LanguageManager.getSavedLanguage(context)
            val isChineseLanguage = currentLanguage == LanguageManager.LANGUAGE_CHINESE ||
                    (currentLanguage == LanguageManager.LANGUAGE_SYSTEM &&
                     Locale.getDefault().language == "zh")

            // 转换为 DateItem 列表
            val dateItems = mutableListOf<DateItem>()

            holidayData.holidays.forEach { holidayItem ->
                try {
                    // 解析日期字符串
                    val date = dateFormat.parse(holidayItem.date)
                        ?: throw IllegalArgumentException("日期格式错误：${holidayItem.date}")

                    // 根据语言选择名称
                    val displayName = if (isChineseLanguage) {
                        holidayItem.nameCn
                    } else {
                        holidayItem.nameEn
                    }

                    // 创建 DateItem
                    val dateItem = DateItem(
                        title = displayName,
                        content = "${holidayData.year}年${jsonRegion.name}节假日",
                        targetDate = date.time,
                        type = DateItemType.SYSTEM_HOLIDAY,
                        region = jsonRegion,
                        isReminderEnabled = false
                    )

                    dateItems.add(dateItem)
                } catch (e: Exception) {
                    // 单个日期解析失败不影响其他日期
                    return ParseResult.Error("解析日期失败：${holidayItem.id} (${e.message})")
                }
            }

            ParseResult.Success(dateItems)

        } catch (e: JsonSyntaxException) {
            ParseResult.Error("JSON 语法错误：${e.message}")
        } catch (e: Exception) {
            ParseResult.Error("解析失败：${e.message}")
        }
    }

    /**
     * 验证 JSON 格式是否正确（不进行完整解析）
     *
     * @param jsonString JSON 字符串内容
     * @return 验证结果消息
     */
    fun validateJsonFormat(jsonString: String): String? {
        return try {
            val holidayData = gson.fromJson(jsonString, HolidayData::class.java)
                ?: return "无效的 JSON 格式"

            when {
                holidayData.year <= 0 -> "年份字段无效"
                holidayData.region.isBlank() -> "地区字段为空"
                holidayData.holidays.isEmpty() -> "节假日列表为空"
                else -> null  // 验证通过
            }
        } catch (e: JsonSyntaxException) {
            "JSON 语法错误：${e.message}"
        } catch (e: Exception) {
            "验证失败：${e.message}"
        }
    }
}
