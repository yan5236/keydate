package com.slcatwujian.keydate.data.repository

import com.slcatwujian.keydate.data.dao.DateItemDao
import com.slcatwujian.keydate.data.models.DateItem
import kotlinx.coroutines.flow.Flow

/**
 * 日期数据仓库
 * 封装数据访问逻辑，为ViewModel提供统一的数据接口
 *
 * @property dateItemDao 日期项DAO对象
 */
class DateRepository(private val dateItemDao: DateItemDao) {

    /**
     * 获取所有日期项的Flow
     * @return 日期项列表的Flow，当数据库数据变化时会自动更新
     */
    val allDateItems: Flow<List<DateItem>> = dateItemDao.getAllDateItems()

    /**
     * 根据ID获取单个日期项
     * @param id 日期项ID
     * @return 对应的日期项，不存在则返回null
     */
    suspend fun getDateItemById(id: Long): DateItem? {
        return dateItemDao.getDateItemById(id)
    }

    /**
     * 插入新的日期项
     * @param dateItem 要插入的日期项
     * @return 插入后的行ID
     */
    suspend fun insert(dateItem: DateItem): Long {
        return dateItemDao.insert(dateItem)
    }

    /**
     * 更新现有的日期项
     * @param dateItem 要更新的日期项
     */
    suspend fun update(dateItem: DateItem) {
        dateItemDao.update(dateItem)
    }

    /**
     * 删除日期项
     * @param dateItem 要删除的日期项
     */
    suspend fun delete(dateItem: DateItem) {
        dateItemDao.delete(dateItem)
    }

    /**
     * 删除所有日期项
     */
    suspend fun deleteAll() {
        dateItemDao.deleteAll()
    }
}
