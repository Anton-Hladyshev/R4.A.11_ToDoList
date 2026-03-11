package com.example.todolist.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolist.R
import com.example.todolist.model.SwordShop
import com.example.todolist.model.Wallet

@Composable
fun SwordUpgradeComponent(swordShop: SwordShop, wallet: Wallet, modifier: Modifier = Modifier) {
    val currentSword = swordShop.currentSword
    val nextSword = swordShop.nextSword

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Épée Actuel",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Image épée actuelle
            if (currentSword.image != 0) {
                Image(
                    painter = painterResource(id = currentSword.image),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${currentSword.material.icon} ${currentSword.material.label}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Grade ${currentSword.material.grade}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        if (nextSword != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = { swordShop.upgradeSword(wallet) },
                    enabled = wallet.balance >= nextSword.price,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Améliorer ${nextSword.price}")
                    Image(
                        painter = painterResource(id = R.drawable.coin),
                        contentDescription = "Coins",
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(24.dp)
                    )
                }

            }
        } else {
            Text(
                text = "Tu possèdes l'épée ultime ! ️",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
