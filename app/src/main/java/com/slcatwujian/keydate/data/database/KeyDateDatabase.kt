package com.slcatwujian.keydate.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.slcatwujian.keydate.data.dao.DateItemDao
import com.slcatwujian.keydate.data.models.DateItem

/**
 * Key Date应用的Room数据库
 * 使用单例模式确保整个应用只有一个数据库实例
 */
@Database(
    entities = [DateItem::class],
    version = 1,
    exportSchema = false
)
abstract class KeyDateDatabase : RoomDatabase() {

    /**
     * 获取DateItem的DAO对象
     */
    abstract fun dateItemDao(): DateItemDao

    companion object {
        @Volatile
        private var INSTANCE: KeyDateDatabase? = null

        /**
         * 获取数据库实例
         * 使用双重检查锁定确保线程安全
         * @param context 应用上下文
         * @return 数据库实例
         */
        fun getDatabase(context: Context): KeyDateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KeyDateDatabase::class.java,
                    "key_date_database"
                )
                    .fallbackToDestructiveMigration() // 简化版本升级，实际生产环境需要提供迁移策略
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
