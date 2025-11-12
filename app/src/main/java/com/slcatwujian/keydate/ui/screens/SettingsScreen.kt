package com.slcatwujian.keydate.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slcatwujian.keydate.MainActivity
import com.slcatwujian.keydate.R
import com.slcatwujian.keydate.ui.theme.KeyDateTheme
import com.slcatwujian.keydate.utils.LanguageManager

/**
 * 语言选项枚举
 */
enum class LanguageOption(val code: String) {
    SYSTEM(LanguageManager.LANGUAGE_SYSTEM),     // 跟随系统
    CHINESE(LanguageManager.LANGUAGE_CHINESE),   // 简体中文
    ENGLISH(LanguageManager.LANGUAGE_ENGLISH)    // English
}

/**
 * 根据语言代码获取对应的枚举值
 */
fun getLanguageOptionFromCode(code: String): LanguageOption {
    return when (code) {
        LanguageManager.LANGUAGE_CHINESE -> LanguageOption.CHINESE
        LanguageManager.LANGUAGE_ENGLISH -> LanguageOption.ENGLISH
        else -> LanguageOption.SYSTEM
    }
}

/**
 * 设置页面
 *
 * 用于配置应用的各种设置选项,包括高级设置和语言选择
 */
@Composable
fun SettingsScreen() {
    val context = LocalContext.current

    // 从SharedPreferences读取当前保存的语言设置
    val savedLanguageCode = remember { LanguageManager.getSavedLanguage(context) }
    var selectedLanguage by remember { mutableStateOf(getLanguageOptionFromCode(savedLanguageCode)) }

    // 是否显示语言选择对话框
    var showLanguageDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 高级设置分组
        Text(
            text = stringResource(R.string.settings_advanced),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 语言设置项
        SettingsItem(
            title = stringResource(R.string.settings_language),
            subtitle = getLanguageDisplayName(selectedLanguage),
            onClick = { showLanguageDialog = true }
        )
    }

    // 语言选择对话框
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = selectedLanguage,
            onLanguageSelected = { language ->
                // 保存语言设置
                LanguageManager.saveLanguage(context, language.code)
                selectedLanguage = language
                showLanguageDialog = false

                // 重启Activity以应用新的语言设置
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
                (context as? Activity)?.finish()
            },
            onDismiss = { showLanguageDialog = false }
        )
    }
}

/**
 * 设置项组件
 *
 * @param title 设置项标题
 * @param subtitle 设置项副标题(当前选项值)
 * @param onClick 点击事件
 */
@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 语言选择对话框
 *
 * @param currentLanguage 当前选中的语言
 * @param onLanguageSelected 语言选择回调
 * @param onDismiss 对话框关闭回调
 */
@Composable
fun LanguageSelectionDialog(
    currentLanguage: LanguageOption,
    onLanguageSelected: (LanguageOption) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_language)) },
        text = {
            Column {
                LanguageOption.entries.forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(language) }
                            .padding(vertical = 12.dp)
                    ) {
                        RadioButton(
                            selected = currentLanguage == language,
                            onClick = { onLanguageSelected(language) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = getLanguageDisplayName(language),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

/**
 * 获取语言选项的显示名称
 *
 * @param language 语言选项
 * @return 对应的显示文本
 */
@Composable
fun getLanguageDisplayName(language: LanguageOption): String {
    return when (language) {
        LanguageOption.SYSTEM -> stringResource(R.string.settings_language_system)
        LanguageOption.CHINESE -> stringResource(R.string.settings_language_chinese)
        LanguageOption.ENGLISH -> stringResource(R.string.settings_language_english)
    }
}

/**
 * 预览函数
 * 用于在 Android Studio 中预览 SettingsScreen
 */
@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    KeyDateTheme {
        SettingsScreen()
    }
}
