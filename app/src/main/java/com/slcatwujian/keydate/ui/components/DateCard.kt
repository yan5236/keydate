package com.slcatwujian.keydate.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slcatwujian.keydate.R
import com.slcatwujian.keydate.data.models.DateItem
import com.slcatwujian.keydate.ui.theme.KeyDateTheme
import com.slcatwujian.keydate.utils.DateTimeUtils

/**
 * 日期卡片组件
 * 显示单个日期项的信息,包括标题、内容、目标日期和倒计时
 *
 * @param dateItem 日期项数据
 * @param currentTime 当前时间戳（用于计算倒计时）
 * @param onDelete 删除回调
 * @param modifier 修饰符
 */
@Composable
fun DateCard(
    dateItem: DateItem,
    currentTime: Long,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // 计算时间差
    val timeDiff = DateTimeUtils.calculateTimeDifference(dateItem.targetDate, currentTime)
    val timeText = DateTimeUtils.formatTimeDifference(context, timeDiff)
    val dateText = DateTimeUtils.formatDateWithWeekday(dateItem.targetDate)

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (timeDiff.isExpired) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题行（包含删除按钮）
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateItem.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (timeDiff.isExpired) TextDecoration.LineThrough else null,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.date_card_delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 内容
            if (dateItem.content.isNotEmpty()) {
                Text(
                    text = dateItem.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 目标日期
            Text(
                text = dateText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 倒计时
            Text(
                text = timeText,
                style = MaterialTheme.typography.bodyLarge,
                color = if (timeDiff.isExpired) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        }
    }
}

/**
 * DateCard预览
 */
@Preview(showBackground = true)
@Composable
fun DateCardPreview() {
    KeyDateTheme {
        Column {
            // 未过期的日期
            DateCard(
                dateItem = DateItem(
                    id = 1,
                    title = "生日派对",
                    content = "记得准备礼物和蛋糕",
                    targetDate = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L // 7天后
                ),
                currentTime = System.currentTimeMillis(),
                onDelete = {},
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 已过期的日期
            DateCard(
                dateItem = DateItem(
                    id = 2,
                    title = "项目截止日期",
                    content = "完成所有功能开发和测试",
                    targetDate = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000L // 2天前
                ),
                currentTime = System.currentTimeMillis(),
                onDelete = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
