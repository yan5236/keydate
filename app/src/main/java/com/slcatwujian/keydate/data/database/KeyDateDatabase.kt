package com.slcatwujian.keydate.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.slcatwujian.keydate.data.dao.DateItemDao
import com.slcatwujian.keydate.data.models.DateItem

/**
 * 数据库迁移：版本 1 -> 2
 * 添加 type 和 region 字段
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 添加 type 字段，默认值为 USER_CREATED
        database.execSQL("ALTER TABLE date_items ADD COLUMN type TEXT NOT NULL DEFAULT 'USER_CREATED'")
        // 添加 region 字段，可为 null
        database.execSQL("ALTER TABLE date_items ADD COLUMN region TEXT")
    }
}

/**
 * 数据库迁移：版本 2 -> 3
 * 更新 type 字段值：USER_CREATED -> DATE（所有非系统节假日的记录）
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 将所有 USER_CREATED 类型更新为 DATE
        database.execSQL("UPDATE date_items SET type = 'DATE' WHERE type = 'USER_CREATED'")
    }
}

/**
 * Key Date应用的Room数据库
 * 使用单例模式确保整个应用只有一个数据库实例
 */
@Database(
    entities = [DateItem::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // 添加数据库迁移策略
                    .fallbackToDestructiveMigration() // 如果没有找到合适的迁移路径，则销毁重建
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

