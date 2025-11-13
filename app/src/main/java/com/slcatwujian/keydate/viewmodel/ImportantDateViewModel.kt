package com.slcatwujian.keydate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.slcatwujian.keydate.data.database.KeyDateDatabase
import com.slcatwujian.keydate.data.models.DateItem
import com.slcatwujian.keydate.data.repository.DateRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 重要日期界面的 ViewModel
 * 管理重要日期数据和状态
 */
class ImportantDateViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DateRepository
    private val _currentTime = MutableStateFlow(System.currentTimeMillis())

    init {
        val database = KeyDateDatabase.getDatabase(application)
        repository = DateRepository(database.dateItemDao())
    }

    /**
     * 当前时间状态
     */
    val currentTime: StateFlow<Long> = _currentTime.asStateFlow()

    /**
     * 所有重要日期（包括用户创建和节假日）
     * 自动排序：未来日期按时间远近升序，已过期日期放在后面
     */
    val allDateItems: StateFlow<List<DateItem>> = repository.importantDateItems
        .map { items -> sortDateItems(items) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * 最近的重要日期（最接近的未来日期）
     */
    val nextImportantDate: StateFlow<DateItem?> = allDateItems
        .map { items ->
            val currentTime = System.currentTimeMillis()
            items.firstOrNull { it.targetDate >= currentTime }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    /**
     * 更新当前时间（由UI每秒调用）
     */
    fun updateCurrentTime() {
        _currentTime.value = System.currentTimeMillis()
    }

    /**
     * 插入新的日期项
     */
    fun insert(dateItem: DateItem) {
        viewModelScope.launch {
            repository.insert(dateItem)
        }
    }

    /**
     * 更新日期项
     */
    fun update(dateItem: DateItem) {
        viewModelScope.launch {
            repository.update(dateItem)
        }
    }

    /**
     * 删除日期项
     */
    fun delete(dateItem: DateItem) {
        viewModelScope.launch {
            repository.delete(dateItem)
        }
    }

    /**
     * 根据ID获取日期项
     */
    suspend fun getDateItemById(id: Long): DateItem? {
        return repository.getDateItemById(id)
    }

    /**
     * 对日期项进行排序
     * 未来日期按时间远近升序，已过期日期放在后面
     */
    private fun sortDateItems(items: List<DateItem>): List<DateItem> {
        val currentTime = System.currentTimeMillis()
        val (future, past) = items.partition { it.targetDate >= currentTime }
        return future.sortedBy { it.targetDate } + past.sortedByDescending { it.targetDate }
    }
}
