package com.slcatwujian.keydate.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.core.net.toUri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slcatwujian.keydate.R
import com.slcatwujian.keydate.ui.theme.KeyDateTheme

/**
 * 关于页面
 *
 * 展示应用信息、版本号、开源代码链接等
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // 应用图标 - 使用drawable中的图标
            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 应用名称
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 版本号
            Text(
                text = stringResource(R.string.version, "1.0.0"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 开源代码
            AboutItem(
                title = stringResource(R.string.open_source_code),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, context.getString(R.string.github_url).toUri())
                    context.startActivity(intent)
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 使用协议
            AboutItem(
                title = stringResource(R.string.terms_of_use),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, context.getString(R.string.terms_url).toUri())
                    context.startActivity(intent)
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 隐私协议
            AboutItem(
                title = stringResource(R.string.privacy_policy),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, context.getString(R.string.privacy_url).toUri())
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 底部版权信息
            Text(
                text = stringResource(R.string.copyright),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 关于页面选项项
 *
 * @param title 选项标题
 * @param onClick 点击事件
 */
@Composable
fun AboutItem(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * 预览函数
 * 用于在 Android Studio 中预览 AboutScreen
 */
@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    KeyDateTheme {
        AboutScreen()
    }
}
