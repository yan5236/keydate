package com.slcatwujian.keydate.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.slcatwujian.keydate.R
import com.slcatwujian.keydate.data.models.DateItem
import com.slcatwujian.keydate.ui.theme.KeyDateTheme
import com.slcatwujian.keydate.utils.DateTimeUtils
import java.util.Calendar

/**
 * 日期编辑BottomSheet组件
 * 提供日期项的创建和编辑功能
 *
 * @param dateItem 要编辑的日期项（null表示新建）
 * @param dateItemType 新建时使用的日期类型（编辑时忽略此参数）
 * @param onDismiss 关闭回调
 * @param onSave 保存回调，参数为DateItem
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateEditBottomSheet(
    dateItem: DateItem? = null,
    dateItemType: com.slcatwujian.keydate.data.models.DateItemType = com.slcatwujian.keydate.data.models.DateItemType.DATE,
    onDismiss: () -> Unit,
    onSave: (DateItem) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // 表单状态
    var title by remember { mutableStateOf(dateItem?.title ?: "") }
    var content by remember { mutableStateOf(dateItem?.content ?: "") }

    // 日期时间状态
    val calendar = remember {
        Calendar.getInstance().apply {
            timeInMillis = dateItem?.targetDate ?: System.currentTimeMillis()
        }
    }
    var selectedYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH) + 1) }
    var selectedDay by remember { mutableIntStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var selectedHour by remember { mutableIntStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableIntStateOf(calendar.get(Calendar.MINUTE)) }

    // 对话框状态
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // 提醒开关（目前禁用）
    var reminderEnabled by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 标题
            Text(
                text = if (dateItem == null) {
                    stringResource(R.string.date_edit_title_new)
                } else {
                    stringResource(R.string.date_edit_title_edit)
                },
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 标题输入框
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.date_edit_title_label)) },
                placeholder = { Text(stringResource(R.string.date_edit_title_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 内容输入框
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text(stringResource(R.string.date_edit_content_label)) },
                placeholder = { Text(stringResource(R.string.date_edit_content_hint)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 日期选择
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.date_edit_date_label),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$selectedYear-${selectedMonth.toString().padStart(2, '0')}-${selectedDay.toString().padStart(2, '0')}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { showDatePicker = true }
                )
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = stringResource(R.string.date_edit_select_date)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 时间选择
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTimePicker = true }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.date_edit_time_label),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${selectedHour.toString().padStart(2, '0')}:${selectedMinute.toString().padStart(2, '0')}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { showTimePicker = true }
                )
                IconButton(onClick = { showTimePicker = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.schedule),
                        contentDescription = stringResource(R.string.date_edit_select_time)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 提醒开关（禁用）
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.date_edit_reminder_label),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(R.string.date_edit_reminder_disabled),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = reminderEnabled,
                    onCheckedChange = { /* 禁用 */ },
                    enabled = false
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 按钮行
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.date_edit_cancel))
                }

                Spacer(modifier = Modifier.padding(horizontal = 8.dp))

                Button(
                    onClick = {
                        if (title.isNotEmpty()) {
                            val timestamp = DateTimeUtils.createTimestamp(
                                selectedYear, selectedMonth, selectedDay,
                                selectedHour, selectedMinute
                            )
                            val savedItem = if (dateItem == null) {
                                DateItem(
                                    title = title,
                                    content = content,
                                    targetDate = timestamp,
                                    type = dateItemType
                                )
                            } else {
                                dateItem.copy(
                                    title = title,
                                    content = content,
                                    targetDate = timestamp
                                )
                            }
                            onSave(savedItem)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = title.isNotEmpty()
                ) {
                    Text(stringResource(R.string.date_edit_save))
                }
            }
        }
    }

    // 日期选择器对话框
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = DateTimeUtils.createTimestamp(
                selectedYear, selectedMonth, selectedDay, 0, 0
            )
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val cal = Calendar.getInstance().apply { timeInMillis = millis }
                        selectedYear = cal.get(Calendar.YEAR)
                        selectedMonth = cal.get(Calendar.MONTH) + 1
                        selectedDay = cal.get(Calendar.DAY_OF_MONTH)
                    }
                    showDatePicker = false
                }) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // 时间选择器对话框
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedHour,
            initialMinute = selectedMinute
        )
        Dialog(onDismissRequest = { showTimePicker = false }) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // 标题
                    Text(
                        text = stringResource(R.string.date_edit_select_time),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    // 时间选择器
                    TimePicker(state = timePickerState)

                    // 按钮行
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = { showTimePicker = false }) {
                            Text(stringResource(android.R.string.cancel))
                        }
                        TextButton(onClick = {
                            selectedHour = timePickerState.hour
                            selectedMinute = timePickerState.minute
                            showTimePicker = false
                        }) {
                            Text(stringResource(android.R.string.ok))
                        }
                    }
                }
            }
        }
    }
}

/**
 * DateEditBottomSheet预览
 */
@Preview
@Composable
fun DateEditBottomSheetPreview() {
    KeyDateTheme {
        DateEditBottomSheet(
            dateItem = null,
            onDismiss = {},
            onSave = { }
        )
    }
}
