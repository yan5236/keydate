package com.slcatwujian.keydate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.slcatwujian.keydate.data.database.KeyDateDatabase
import com.slcatwujian.keydate.data.models.DateItem
import com.slcatwujian.keydate.data.repository.DateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 日期界面的ViewModel
 * 管理日期列表的状态和业务逻辑
 *
 * @property application 应用上下文
 */
class DateViewModel(application: Application) : AndroidViewModel(application) {

    // 初始化数据库和仓库
    private val repository: DateRepository

    init {
        val dateItemDao = KeyDateDatabase.getDatabase(application).dateItemDao()
        repository = DateRepository(dateItemDao)
    }

    // 日期列表状态（按时间远近排序）
    private val _dateItems = MutableStateFlow<List<DateItem>>(emptyList())
    val dateItems: StateFlow<List<DateItem>> = _dateItems.asStateFlow()

    // 当前时间戳，用于实时计算倒计时
    private val _currentTime = MutableStateFlow(System.currentTimeMillis())
    val currentTime: StateFlow<Long> = _currentTime.asStateFlow()

    init {
        // 监听数据库变化并排序
        viewModelScope.launch {
            repository.allDateItems.collect { items ->
                _dateItems.value = sortDateItems(items)
            }
        }
    }

    /**
     * 更新当前时间（由UI层定期调用以实现实时倒计时）
     */
    fun updateCurrentTime() {
        _currentTime.value = System.currentTimeMillis()
    }

    /**
     * 排序日期项：未来的日期按时间远近升序，已过期的日期放在后面
     * @param items 原始日期列表
     * @return 排序后的日期列表
     */
    private fun sortDateItems(items: List<DateItem>): List<DateItem> {
        val currentTime = System.currentTimeMillis()
        val (future, past) = items.partition { it.targetDate >= currentTime }
        return future.sortedBy { it.targetDate } + past.sortedBy { it.targetDate }
    }

    /**
     * 插入新的日期项
     * @param dateItem 要插入的日期项
     */
    fun insertDateItem(dateItem: DateItem) {
        viewModelScope.launch {
            repository.insert(dateItem)
        }
    }

    /**
     * 更新日期项
     * @param dateItem 要更新的日期项
     */
    fun updateDateItem(dateItem: DateItem) {
        viewModelScope.launch {
            repository.update(dateItem)
        }
    }

    /**
     * 删除日期项
     * @param dateItem 要删除的日期项
     */
    fun deleteDateItem(dateItem: DateItem) {
        viewModelScope.launch {
            repository.delete(dateItem)
        }
    }

    /**
     * 判断日期是否已过期
     * @param targetDate 目标日期时间戳
     * @return true表示已过期，false表示未过期
     */
    fun isDateExpired(targetDate: Long): Boolean {
        return targetDate < currentTime.value
    }
}
