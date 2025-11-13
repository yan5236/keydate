package com.slcatwujian.keydate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slcatwujian.keydate.R
import com.slcatwujian.keydate.data.models.DateItem
import com.slcatwujian.keydate.utils.CountdownFormatter
import com.slcatwujian.keydate.utils.DateTimeUtils

/**
 * 最近重要日大卡片组件
 * 显示最接近的重要日期和大号倒计时
 *
 * @param dateItem 最近的重要日期项
 * @param currentTime 当前时间戳
 * @param onClick 点击事件（可选,点击卡片查看详情）
 */
@Composable
fun NextImportantDateCard(
    dateItem: DateItem?,
    currentTime: Long,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        onClick = onClick
    ) {
        if (dateItem == null) {
            // 空状态
            EmptyNextDateContent()
        } else {
            // 显示倒计时
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 标题
                Text(
                    text = stringResource(R.string.next_important_date),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 日期标题
                Text(
                    text = dateItem.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 大号倒计时
                val countdownData = CountdownFormatter.formatSmart(dateItem.targetDate, currentTime)
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // 数值
                    Text(
                        text = countdownData.value.toString(),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 72.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (countdownData.isExpired) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // 单位
                    Text(
                        text = CountdownFormatter.getUnitString(context, dateItem.targetDate, currentTime),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 状态文本（还有/已过）
                Text(
                    text = if (countdownData.isExpired) {
                        stringResource(R.string.date_expired)
                    } else {
                        stringResource(R.string.date_remaining)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 目标日期
                Text(
                    text = DateTimeUtils.formatDateWithWeekday(dateItem.targetDate),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * 空状态内容
 */
@Composable
private fun EmptyNextDateContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_important_dates),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.add_important_date_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
    }
}
