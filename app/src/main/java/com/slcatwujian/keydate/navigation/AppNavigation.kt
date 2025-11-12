package com.slcatwujian.keydate.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.slcatwujian.keydate.ui.screens.DateScreen
import com.slcatwujian.keydate.ui.screens.ImportantDateScreen
import com.slcatwujian.keydate.ui.screens.SettingsScreen

/**
 * 应用主导航配置
 *
 * 负责管理所有页面的路由和导航逻辑
 *
 * @param navController 导航控制器,用于控制页面跳转
 * @param modifier 修饰符,用于调整布局
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavigationItem.Date.route, // 设置默认启动页面为日期页面
        modifier = modifier
    ) {
        // 日期页面路由
        composable(NavigationItem.Date.route) {
            DateScreen()
        }

        // 重要日页面路由
        composable(NavigationItem.ImportantDate.route) {
            ImportantDateScreen()
        }

        // 设置页面路由
        composable(NavigationItem.Settings.route) {
            SettingsScreen()
        }
    }
}
