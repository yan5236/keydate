package com.slcatwujian.keydate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.slcatwujian.keydate.navigation.AppNavigation
import com.slcatwujian.keydate.navigation.NavigationItem
import com.slcatwujian.keydate.ui.theme.KeyDateTheme

/**
 * 主Activity
 *
 * 应用的入口点,负责设置主题和底部导航栏
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KeyDateTheme {
                MainScreen()
            }
        }
    }
}

/**
 * 主屏幕组件
 *
 * 包含底部导航栏和主要内容区域
 */
@Composable
fun MainScreen() {
    // 创建导航控制器
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            // 底部导航栏
            NavigationBar {
                // 获取当前导航状态
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // 遍历所有导航项并显示
                NavigationItem.items.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            // 根据图标类型使用不同的加载方式
                            when {
                                // 如果有 ImageVector 图标,使用矢量图标
                                item.iconVector != null -> {
                                    Icon(
                                        imageVector = item.iconVector,
                                        contentDescription = item.title
                                    )
                                }
                                // 如果有 Drawable 资源图标,使用资源图标
                                item.iconRes != null -> {
                                    Icon(
                                        painter = painterResource(id = item.iconRes),
                                        contentDescription = item.title
                                    )
                                }
                            }
                        },
                        label = { Text(item.title) },
                        // 判断当前项是否被选中
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            // 点击导航项时跳转到对应页面
                            navController.navigate(item.route) {
                                // 避免在导航栈中创建多个相同实例
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // 避免重复点击相同项时创建多个副本
                                launchSingleTop = true
                                // 恢复状态
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // 主内容区域,使用导航组件管理不同页面
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}