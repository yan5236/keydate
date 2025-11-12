package com.slcatwujian.keydate.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

/**
 * 语言管理工具类
 *
 * 负责保存、读取和应用语言设置
 */
object LanguageManager {
    private const val PREFS_NAME = "language_settings"
    private const val KEY_LANGUAGE = "selected_language"

    // 语言代码常量
    const val LANGUAGE_SYSTEM = "system"
    const val LANGUAGE_CHINESE = "zh"
    const val LANGUAGE_ENGLISH = "en"

    /**
     * 保存语言设置
     */
    fun saveLanguage(context: Context, languageCode: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, languageCode)
            .apply()
    }

    /**
     * 获取保存的语言设置
     */
    fun getSavedLanguage(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, LANGUAGE_SYSTEM) ?: LANGUAGE_SYSTEM
    }

    /**
     * 应用语言设置到Context
     *
     * @return 应用了新语言设置的Context
     */
    fun applyLanguage(context: Context, languageCode: String): Context {
        val locale = when (languageCode) {
            LANGUAGE_CHINESE -> Locale.SIMPLIFIED_CHINESE
            LANGUAGE_ENGLISH -> Locale.ENGLISH
            else -> return context // 跟随系统,返回原始context
        }

        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }

    /**
     * 应用已保存的语言设置
     */
    fun applySavedLanguage(context: Context): Context {
        val savedLanguage = getSavedLanguage(context)
        return applyLanguage(context, savedLanguage)
    }
}
