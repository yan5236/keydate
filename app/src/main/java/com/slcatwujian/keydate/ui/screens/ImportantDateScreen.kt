package com.slcatwujian.keydate.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.slcatwujian.keydate.R
import com.slcatwujian.keydate.ui.theme.KeyDateTheme

/**
 * 重要日页面
 *
 * 用于展示和管理重要日期,如生日、节假日、纪念日等
 */
@Composable
fun ImportantDateScreen() {
    // 使用 Box 布局将文字居中显示
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(R.string.important_date_screen_placeholder))
    }
}

/**
 * 预览函数
 * 用于在 Android Studio 中预览 ImportantDateScreen
 */
@Preview(showBackground = true)
@Composable
fun ImportantDateScreenPreview() {
    KeyDateTheme {
        ImportantDateScreen()
    }
}
