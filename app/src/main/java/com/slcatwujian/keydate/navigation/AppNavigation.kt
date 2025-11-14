package com.slcatwujian.keydate.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.slcatwujian.keydate.ui.screens.AboutScreen
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
        modifier = modifier,
        // 为所有页面设置无动画效果（硬切）
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
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
            SettingsScreen(
                onNavigateToAbout = {
                    navController.navigate("about")
                }
            )
        }

        // 关于页面路由 - 使用自定义滑动动画
        composable(
            route = "about",
            // 进入动画：从右往左滑入
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            // 退出动画：不设置（当前页面消失时没有动画）
            exitTransition = { ExitTransition.None },
            // 返回时的进入动画：不设置（返回到设置页面时没有动画）
            popEnterTransition = { EnterTransition.None },
            // 返回时的退出动画：从左往右滑出
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            AboutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
