package com.example.todolist.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomProgressBar(
    currentValue: Int,
    maxValue: Int,
    color: Color,
    modifier: Modifier = Modifier,
    label: String = "",
    height: Dp = 20.dp,
    textColor: Color = Color.Black,
    showText: Boolean = true
) {
    val progress by animateFloatAsState(
        targetValue = if (maxValue > 0) currentValue.toFloat() / maxValue else 0f,
        label = "progressAnimation"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = textColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(height / 2))
                .background(Color.Gray.copy(alpha = 0.2f))
                .border(1.dp, Color.Black.copy(alpha = 0.1f), RoundedCornerShape(height / 2))
        ) {
            // Progress fill
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(height / 2))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                color.copy(alpha = 0.8f),
                                color
                            )
                        )
                    )
            )

            if (showText) {
                // Value text overlay - Ensuring it's at least 10sp
                val calculatedFontSize = (height.value * 0.7).sp
                val fontSize = if (calculatedFontSize.value < 10f) 10.sp else calculatedFontSize

                Text(
                    text = "$currentValue / $maxValue",
                    modifier = Modifier.align(Alignment.Center),
                    color = textColor,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
