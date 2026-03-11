package com.example.todolist.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolist.R
import com.example.todolist.model.Wallet

@Composable
fun CoinsView(wallet: Wallet, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .border(width = 2.dp, color = colorResource(id = R.color.golden), shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(id = R.color.orange))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.coin),
            contentDescription = "Coins",
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "${wallet.balance}",
            style = MaterialTheme.typography.titleMedium.copy(
                color = colorResource(id = R.color.golden),
                fontSize = 18.sp
            )
        )
    }
}
