package com.slcatwujian.keydate.utils

import android.content.Context
import android.net.Uri
import com.slcatwujian.keydate.data.models.DateItem
import com.slcatwujian.keydate.data.models.DateItemType
import com.slcatwujian.keydate.data.models.DateRegion
import com.slcatwujian.keydate.data.repository.DateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * 节假日管理器
 * 负责加载、导入和管理节假日数据
 */
class HolidayManager(
    private val context: Context,
    private val repository: DateRepository
) {

    private val holidayParser = HolidayParser(context)

    /**
     * 从 Assets 加载内置节假日
     *
     * @param region 地区类型
     * @return 加载结果
     */
    suspend fun loadBuiltInHolidays(region: DateRegion): Result<List<DateItem>> = withContext(Dispatchers.IO) {
        try {
            // 确定文件名
            val fileName = when (region) {
                DateRegion.CHINA -> "holidays/china_2025.json"
                DateRegion.GLOBAL -> "holidays/global_2025.json"
            }

            // 读取 Assets 文件
            val jsonString = context.assets.open(fileName).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            }

            // 解析 JSON
            when (val result = holidayParser.parseHolidayJson(jsonString, region)) {
                is HolidayParser.ParseResult.Success -> {
                    Result.success(result.dateItems)
                }
                is HolidayParser.ParseResult.Error -> {
                    Result.failure(Exception(result.message))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("加载内置节假日失败: ${e.message}"))
        }
    }

    /**
     * 从外部文件 URI 导入节假日
     *
     * @param uri 文件 URI
     * @param region 用户选择的地区
     * @return 导入结果
     */
    suspend fun importHolidaysFromUri(uri: Uri, region: DateRegion): Result<Int> = withContext(Dispatchers.IO) {
        try {
            // 读取文件内容
            val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            } ?: return@withContext Result.failure(Exception("无法读取文件"))

            // 解析 JSON
            when (val result = holidayParser.parseHolidayJson(jsonString, region)) {
                is HolidayParser.ParseResult.Success -> {
                    // 先删除该地区的旧节假日
                    deleteHolidaysByRegion(region)

                    // 插入新节假日
                    result.dateItems.forEach { dateItem ->
                        repository.insert(dateItem)
                    }

                    Result.success(result.dateItems.size)
                }
                is HolidayParser.ParseResult.Error -> {
                    Result.failure(Exception(result.message))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("导入失败: ${e.message}"))
        }
    }

    /**
     * 加载并保存内置节假日到数据库
     *
     * @param region 地区类型
     * @return 导入的节假日数量
     */
    suspend fun importBuiltInHolidays(region: DateRegion): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val result = loadBuiltInHolidays(region)
            result.fold(
                onSuccess = { dateItems ->
                    // 先删除该地区的旧节假日
                    deleteHolidaysByRegion(region)

                    // 插入新节假日
                    dateItems.forEach { dateItem ->
                        repository.insert(dateItem)
                    }

                    return@withContext Result.success(dateItems.size)
                },
                onFailure = { error ->
                    return@withContext Result.failure(Exception("加载内置节假日失败: ${error.message}"))
                }
            )
        } catch (e: Exception) {
            return@withContext Result.failure(Exception("导入内置节假日失败: ${e.message}"))
        }
    }

    /**
     * 删除指定地区的所有节假日
     */
    private suspend fun deleteHolidaysByRegion(region: DateRegion) {
        val dao = repository.dateItemDao
        dao.deleteByTypeAndRegion(
            type = DateItemType.SYSTEM_HOLIDAY.name,
            region = region.name
        )
    }

    /**
     * 获取指定地区的节假日数量
     */
    suspend fun getHolidayCount(region: DateRegion): Int = withContext(Dispatchers.IO) {
        try {
            val dao = repository.dateItemDao
            dao.countByTypeAndRegion(
                type = DateItemType.SYSTEM_HOLIDAY.name,
                region = region.name
            )
        } catch (_: Exception) {
            0
        }
    }

    /**
     * 创建"距离明年"系统日期项
     */
    suspend fun createNewYearCountdown(): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            val nextYear = currentYear + 1

            // 创建明年1月1日的时间戳
            val calendar = java.util.Calendar.getInstance()
            calendar.set(nextYear, 0, 1, 0, 0, 0)  // 月份从0开始,0代表1月
            calendar.set(java.util.Calendar.MILLISECOND, 0)

            val dateItem = DateItem(
                title = "距离${nextYear}年",
                content = "新年倒计时",
                targetDate = calendar.timeInMillis,
                type = DateItemType.SYSTEM_HOLIDAY,
                region = null  // 新年倒计时不区分地区
            )

            val id = repository.insert(dateItem)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(Exception("创建新年倒计时失败: ${e.message}"))
        }
    }
}
