package com.slcatwujian.keydate.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.slcatwujian.keydate.data.models.DateItem
import kotlinx.coroutines.flow.Flow

/**
 * 日期项数据访问对象
 * 提供对DateItem表的CRUD操作
 */
@Dao
interface DateItemDao {
    /**
     * 查询所有日期项
     * @return Flow类型的日期列表，支持实时更新
     */
    @Query("SELECT * FROM date_items ORDER BY targetDate ASC")
    fun getAllDateItems(): Flow<List<DateItem>>

    /**
     * 根据ID查询单个日期项
     * @param id 日期项ID
     * @return 对应的日期项，如果不存在则返回null
     */
    @Query("SELECT * FROM date_items WHERE id = :id")
    suspend fun getDateItemById(id: Long): DateItem?

    /**
     * 插入新的日期项
     * @param dateItem 要插入的日期项
     * @return 插入后的行ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dateItem: DateItem): Long

    /**
     * 更新现有的日期项
     * @param dateItem 要更新的日期项
     */
    @Update
    suspend fun update(dateItem: DateItem)

    /**
     * 删除日期项
     * @param dateItem 要删除的日期项
     */
    @Delete
    suspend fun delete(dateItem: DateItem)

    /**
     * 删除所有日期项
     */
    @Query("DELETE FROM date_items")
    suspend fun deleteAll()
}
