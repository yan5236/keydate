package com.slcatwujian.keydate.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slcatwujian.keydate.R
import com.slcatwujian.keydate.data.models.DateItem
import com.slcatwujian.keydate.ui.components.DateCard
import com.slcatwujian.keydate.ui.components.DateEditBottomSheet
import com.slcatwujian.keydate.ui.theme.KeyDateTheme
import com.slcatwujian.keydate.viewmodel.DateViewModel
import kotlinx.coroutines.delay

/**
 * 日期页面
 *
 * 用于展示和管理普通日期
 * 功能包括:
 * - 显示所有日期的列表（按时间远近排序）
 * - 实时倒计时显示
 * - 添加新日期
 * - 删除日期
 */
@Composable
fun DateScreen(
    viewModel: DateViewModel = viewModel()
) {
    val dateItems by viewModel.dateItems.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()
    var showEditSheet by remember { mutableStateOf(false) }
    var editingDateItem by remember { mutableStateOf<DateItem?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deletingDateItem by remember { mutableStateOf<DateItem?>(null) }

    // 实时更新当前时间（每秒更新一次）
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.updateCurrentTime()
            delay(1000) // 每秒更新
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingDateItem = null
                    showEditSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = stringResource(R.string.date_add_button)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (dateItems.isEmpty()) {
                // 空状态提示
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.date_empty_message),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // 日期列表
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = dateItems,
                        key = { it.id }
                    ) { dateItem ->
                        DateCard(
                            dateItem = dateItem,
                            currentTime = currentTime,
                            onEdit = {
                                editingDateItem = dateItem
                                showEditSheet = true
                            },
                            onDelete = {
                                deletingDateItem = dateItem
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // 编辑BottomSheet
    if (showEditSheet) {
        DateEditBottomSheet(
            dateItem = editingDateItem,
            onDismiss = {
                showEditSheet = false
                editingDateItem = null
            },
            onSave = { dateItem ->
                if (editingDateItem != null) {
                    // 编辑模式：更新现有项
                    viewModel.updateDateItem(dateItem)
                } else {
                    // 新建模式：插入新项
                    viewModel.insertDateItem(dateItem)
                }
                showEditSheet = false
                editingDateItem = null
            }
        )
    }

    // 删除确认对话框
    if (showDeleteDialog && deletingDateItem != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                deletingDateItem = null
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_warning),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(text = stringResource(R.string.delete_dialog_title))
            },
            text = {
                Text(text = stringResource(R.string.delete_dialog_message, deletingDateItem!!.title))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteDateItem(deletingDateItem!!)
                        showDeleteDialog = false
                        deletingDateItem = null
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete_dialog_confirm),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        deletingDateItem = null
                    }
                ) {
                    Text(text = stringResource(R.string.delete_dialog_cancel))
                }
            }
        )
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
