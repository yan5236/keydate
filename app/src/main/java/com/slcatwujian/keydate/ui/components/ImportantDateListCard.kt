package com.slcatwujian.keydate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.slcatwujian.keydate.data.models.DateItem
import com.slcatwujian.keydate.data.models.DateItemType
import com.slcatwujian.keydate.utils.CountdownFormatter
import com.slcatwujian.keydate.utils.DateTimeUtils

/**
 * 重要日列表卡片组件
 * 用于在列表中显示重要日期
 *
 * @param dateItem 日期项
 * @param currentTime 当前时间戳
 * @param onClick 点击编辑
 * @param onDelete 点击删除（仅用户创建的日期显示删除按钮）
 */
@Composable
fun ImportantDateListCard(
    dateItem: DateItem,
    currentTime: Long,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val isExpired = CountdownFormatter.isExpired(dateItem.targetDate, currentTime)
    val isSystemHoliday = dateItem.type == DateItemType.SYSTEM_HOLIDAY

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpired) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧内容
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 标题行
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 节假日标识
                    if (isSystemHoliday) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Holiday",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    // 标题
                    Text(
                        text = dateItem.title,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (isExpired) TextDecoration.LineThrough else null,
                        color = if (isExpired) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 日期
                Text(
                    text = DateTimeUtils.formatDate(dateItem.targetDate),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 倒计时
                Text(
                    text = CountdownFormatter.format(context, dateItem.targetDate, currentTime),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isExpired) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }

            // 右侧删除按钮（仅用户创建的日期显示）
            if (!isSystemHoliday) {
                IconButton(
                    onClick = onDelete
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
