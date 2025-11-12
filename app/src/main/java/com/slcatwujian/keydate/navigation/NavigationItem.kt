package com.slcatwujian.keydate.navigation

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.slcatwujian.keydate.R

/**
 * 底部导航栏的导航项封装类
 *
 * @property route 导航路由,用于标识不同的页面
 * @property title 导航项标题,显示在底部导航栏
 * @property iconVector Material Icons 矢量图标(可选)
 * @property iconRes Drawable 资源图标(可选)
 */
sealed class NavigationItem(
    val route: String,
    val title: String,
    val iconVector: ImageVector? = null,
    @DrawableRes val iconRes: Int? = null
) {
    /**
     * 日期页面导航项
     * 使用日历图标表示(来自 drawable 资源)
     */
    data object Date : NavigationItem(
        route = "date",
        title = "日期",
        iconRes = R.drawable.calendar_month
    )

    /**
     * 重要日页面导航项
     * 使用星标图标表示(来自 Material Icons)
     */
    data object ImportantDate : NavigationItem(
        route = "important_date",
        title = "重要日",
        iconVector = Icons.Default.Star
    )

    /**
     * 设置页面导航项
     * 使用设置图标表示(来自 Material Icons)
     */
    data object Settings : NavigationItem(
        route = "settings",
        title = "设置",
        iconVector = Icons.Default.Settings
    )

    companion object {
        /**
         * 所有导航项的列表
         * 用于在底部导航栏中遍历显示
         */
        val items = listOf(Date, ImportantDate, Settings)
    }
}
