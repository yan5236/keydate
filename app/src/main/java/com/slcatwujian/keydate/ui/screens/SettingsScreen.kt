package com.slcatwujian.keydate.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slcatwujian.keydate.MainActivity
import com.slcatwujian.keydate.R
import com.slcatwujian.keydate.data.database.KeyDateDatabase
import com.slcatwujian.keydate.data.models.DateRegion
import com.slcatwujian.keydate.data.repository.DateRepository
import com.slcatwujian.keydate.ui.theme.KeyDateTheme
import com.slcatwujian.keydate.utils.HolidayManager
import com.slcatwujian.keydate.utils.LanguageManager
import kotlinx.coroutines.launch

/**
 * 语言选项枚举
 */
enum class LanguageOption(val code: String) {
    SYSTEM(LanguageManager.LANGUAGE_SYSTEM),     // 跟随系统
    CHINESE(LanguageManager.LANGUAGE_CHINESE),   // 简体中文
    ENGLISH(LanguageManager.LANGUAGE_ENGLISH)    // English
}

/**
 * 清除数据类型枚举
 */
enum class ClearDataType {
    DATES,              // 日期数据
    IMPORTANT_DATES     // 重要日期数据
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
 * 用于配置应用的各种设置选项,包括基本设置、高级设置和语言选择
 */
@Composable
fun SettingsScreen(
    onNavigateToAbout: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 初始化 HolidayManager
    val holidayManager = remember {
        val database = KeyDateDatabase.getDatabase(context)
        val repository = DateRepository(database.dateItemDao())
        HolidayManager(context, repository)
    }

    // 从SharedPreferences读取当前保存的语言设置
    val savedLanguageCode = remember { LanguageManager.getSavedLanguage(context) }
    var selectedLanguage by remember { mutableStateOf(getLanguageOptionFromCode(savedLanguageCode)) }

    // 对话框状态
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showRegionDialog by remember { mutableStateOf(false) }
    var showYearDialog by remember { mutableStateOf(false) }
    var showResultSnackbar by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedRegion by remember { mutableStateOf<DateRegion?>(null) }
    var isBuiltInImport by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var clearDataType by remember { mutableStateOf<ClearDataType?>(null) }

    // 文件选择器
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedFileUri = uri
            isBuiltInImport = false
            showRegionDialog = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 基本设置分组
        Text(
            text = stringResource(R.string.settings_basic),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 导入内置节假日
        SettingsItem(
            title = stringResource(R.string.import_builtin_holidays),
            subtitle = stringResource(R.string.import_builtin_holidays_desc),
            onClick = {
                isBuiltInImport = true
                showRegionDialog = true
            }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        // 从文件导入节假日
        SettingsItem(
            title = stringResource(R.string.import_holidays_from_file),
            subtitle = stringResource(R.string.import_holidays_from_file_desc),
            onClick = {
                filePickerLauncher.launch(arrayOf("application/json"))
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

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

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        // 清除数据选项
        SettingsItem(
            title = stringResource(R.string.clear_data),
            subtitle = stringResource(R.string.clear_data_desc),
            onClick = { showClearDataDialog = true }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 其他设置分组
        Text(
            text = stringResource(R.string.settings_other),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 关于选项
        SettingsItem(
            title = stringResource(R.string.about_title),
            subtitle = stringResource(R.string.about_desc),
            onClick = onNavigateToAbout
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

    // 地区选择对话框
    if (showRegionDialog) {
        RegionSelectionDialog(
            onRegionSelected = { region ->
                selectedRegion = region
                showRegionDialog = false

                if (isBuiltInImport) {
                    // 内置导入：显示年份选择对话框
                    showYearDialog = true
                } else if (selectedFileUri != null) {
                    // 文件导入：直接导入
                    scope.launch {
                        val result = holidayManager.importHolidaysFromUri(selectedFileUri!!, region)
                        resultMessage = result.fold(
                            onSuccess = { count -> context.getString(R.string.import_success, count) },
                            onFailure = { e -> context.getString(R.string.import_failed, e.message ?: "") }
                        )
                        showResultSnackbar = true
                        selectedFileUri = null
                    }
                }
            },
            onDismiss = {
                showRegionDialog = false
                selectedFileUri = null
                isBuiltInImport = false
            }
        )
    }

    // 年份选择对话框
    if (showYearDialog && selectedRegion != null) {
        YearSelectionDialog(
            onYearSelected = { year ->
                scope.launch {
                    val result = holidayManager.importBuiltInHolidays(selectedRegion!!)
                    resultMessage = result.fold(
                        onSuccess = { count -> context.getString(R.string.import_success, count) },
                        onFailure = { e -> context.getString(R.string.import_failed, e.message ?: "") }
                    )
                    showResultSnackbar = true
                    showYearDialog = false
                    selectedRegion = null
                    isBuiltInImport = false
                }
            },
            onDismiss = {
                showYearDialog = false
                selectedRegion = null
                isBuiltInImport = false
            }
        )
    }

    // 清除数据类型选择对话框
    if (showClearDataDialog) {
        ClearDataSelectionDialog(
            onDataTypeSelected = { dataType ->
                clearDataType = dataType
                showClearDataDialog = false
                showClearConfirmDialog = true
            },
            onDismiss = {
                showClearDataDialog = false
            }
        )
    }

    // 清除数据确认对话框
    if (showClearConfirmDialog && clearDataType != null) {
        ClearDataConfirmDialog(
            dataType = clearDataType!!,
            onConfirm = {
                scope.launch {
                    try {
                        val database = KeyDateDatabase.getDatabase(context)
                        val repository = DateRepository(database.dateItemDao())

                        when (clearDataType) {
                            ClearDataType.DATES -> {
                                // 清除日期数据（日期页面的数据）
                                repository.deleteByType("DATE")
                            }
                            ClearDataType.IMPORTANT_DATES -> {
                                // 清除重要日期数据（重要日期页面的数据，包括用户创建和系统节假日）
                                repository.deleteByType("IMPORTANT_DATE")
                                repository.deleteByType("SYSTEM_HOLIDAY")
                            }
                            null -> {}
                        }

                        resultMessage = context.getString(R.string.clear_success)
                    } catch (e: Exception) {
                        resultMessage = context.getString(R.string.clear_failed, e.message ?: "")
                    }

                    showResultSnackbar = true
                    showClearConfirmDialog = false
                    clearDataType = null
                }
            },
            onDismiss = {
                showClearConfirmDialog = false
                clearDataType = null
            }
        )
    }

    // 结果提示
    if (showResultSnackbar) {
        LaunchedEffect(resultMessage) {
            showResultSnackbar = false
        }
        Snackbar(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(resultMessage)
        }
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
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentLanguage == language,
                            onClick = { onLanguageSelected(language) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = getLanguageDisplayName(language),
                            style = MaterialTheme.typography.bodyLarge
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
 * 地区选择对话框
 *
 * @param onRegionSelected 地区选择回调
 * @param onDismiss 对话框关闭回调
 */
@Composable
fun RegionSelectionDialog(
    onRegionSelected: (DateRegion) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_holiday_region)) },
        text = {
            Column {
                // 中国
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRegionSelected(DateRegion.CHINA) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.region_china),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                HorizontalDivider()

                // 全球
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRegionSelected(DateRegion.GLOBAL) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.region_global),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

/**
 * 年份选择对话框
 *
 * @param onYearSelected 年份选择回调
 * @param onDismiss 对话框关闭回调
 */
@Composable
fun YearSelectionDialog(
    onYearSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    // 当前只有2025年的数据
    val availableYears = listOf(2025)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_holiday_year)) },
        text = {
            Column {
                availableYears.forEach { year ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onYearSelected(year) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${year}年",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

/**
 * 清除数据类型选择对话框
 *
 * @param onDataTypeSelected 数据类型选择回调
 * @param onDismiss 对话框关闭回调
 */
@Composable
fun ClearDataSelectionDialog(
    onDataTypeSelected: (ClearDataType) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.clear_data_dialog_title)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.clear_data_dialog_message),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 清除日期数据
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDataTypeSelected(ClearDataType.DATES) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.clear_dates),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                HorizontalDivider()

                // 清除重要日期数据
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDataTypeSelected(ClearDataType.IMPORTANT_DATES) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.clear_important_dates),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

/**
 * 清除数据确认对话框
 *
 * @param dataType 要清除的数据类型
 * @param onConfirm 确认回调
 * @param onDismiss 取消回调
 */
@Composable
fun ClearDataConfirmDialog(
    dataType: ClearDataType,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val (title, message) = when (dataType) {
        ClearDataType.DATES -> {
            stringResource(R.string.clear_dates_confirm_title) to
            stringResource(R.string.clear_dates_confirm_message)
        }
        ClearDataType.IMPORTANT_DATES -> {
            stringResource(R.string.clear_important_dates_confirm_title) to
            stringResource(R.string.clear_important_dates_confirm_message)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
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
