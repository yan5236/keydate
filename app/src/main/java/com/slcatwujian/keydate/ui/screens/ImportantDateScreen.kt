package com.slcatwujian.keydate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slcatwujian.keydate.R
import com.slcatwujian.keydate.data.models.DateItem
import com.slcatwujian.keydate.data.models.DateItemType
import com.slcatwujian.keydate.ui.components.DateEditBottomSheet
import com.slcatwujian.keydate.ui.components.ImportantDateListCard
import com.slcatwujian.keydate.ui.components.NextImportantDateCard
import com.slcatwujian.keydate.ui.theme.KeyDateTheme
import com.slcatwujian.keydate.viewmodel.ImportantDateViewModel
import kotlinx.coroutines.delay

/**
 * 重要日页面
 *
 * 用于展示和管理重要日期,包括用户创建的日期和系统节假日
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportantDateScreen(
    viewModel: ImportantDateViewModel = viewModel()
) {
    val dateItems by viewModel.allDateItems.collectAsState()
    val nextDate by viewModel.nextImportantDate.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()

    var showBottomSheet by remember { mutableStateOf(false) }
    var editingDateItem by remember { mutableStateOf<DateItem?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deletingDateItem by remember { mutableStateOf<DateItem?>(null) }

    // 每秒更新当前时间
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.updateCurrentTime()
            delay(1000)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingDateItem = null
                    showBottomSheet = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_date))
            }
        }
    ) { paddingValues ->
        if (dateItems.isEmpty()) {
            // 空状态
            EmptyImportantDateContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            // 内容列表
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // 顶部大卡片：最近的重要日
                item {
                    NextImportantDateCard(
                        dateItem = nextDate,
                        currentTime = currentTime,
                        onClick = {
                            nextDate?.let {
                                editingDateItem = it
                                showBottomSheet = true
                            }
                        }
                    )
                }

                // 重要日列表
                items(
                    items = dateItems,
                    key = { it.id }
                ) { dateItem ->
                    ImportantDateListCard(
                        dateItem = dateItem,
                        currentTime = currentTime,
                        onClick = {
                            editingDateItem = dateItem
                            showBottomSheet = true
                        },
                        onDelete = {
                            deletingDateItem = dateItem
                            showDeleteDialog = true
                        }
                    )
                }

                // 底部间距（避免被FAB遮挡）
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // 编辑底部表单
    if (showBottomSheet) {
        DateEditBottomSheet(
            dateItem = editingDateItem,
            dateItemType = DateItemType.IMPORTANT_DATE,
            onDismiss = {
                showBottomSheet = false
                editingDateItem = null
            },
            onSave = { dateItem ->
                if (editingDateItem == null) {
                    viewModel.insert(dateItem)
                } else {
                    viewModel.update(dateItem)
                }
                showBottomSheet = false
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
            title = {
                Text(text = stringResource(R.string.delete_date_title))
            },
            text = {
                Text(
                    text = stringResource(
                        R.string.delete_date_message,
                        deletingDateItem?.title ?: ""
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        deletingDateItem?.let { viewModel.delete(it) }
                        showDeleteDialog = false
                        deletingDateItem = null
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        deletingDateItem = null
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

/**
 * 空状态内容
 */
@Composable
private fun EmptyImportantDateContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = stringResource(R.string.no_important_dates),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.add_important_date_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 预览函数
 */
@Preview(showBackground = true)
@Composable
fun ImportantDateScreenPreview() {
    KeyDateTheme {
        ImportantDateScreen()
    }
}
