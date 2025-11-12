package com.slcatwujian.keydate.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.slcatwujian.keydate.ui.theme.KeyDateTheme

/**
 * 日期页面
 *
 * 用于展示和管理普通日期
 * 当前为待开发状态,仅显示占位文字
 */
@Composable
fun DateScreen() {
    // 使用 Box 布局将文字居中显示
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "日期页面待开发")
    }
}

/**
 * 预览函数
 * 用于在 Android Studio 中预览 DateScreen
 */
@Preview(showBackground = true)
@Composable
fun DateScreenPreview() {
    KeyDateTheme {
        DateScreen()
    }
}
