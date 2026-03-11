package com.example.todolist.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.todolist.model.SwordShop
import com.example.todolist.model.Wallet
import com.example.todolist.ui.components.CoinsView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwordShopScreen(navController: NavController, swordShop: SwordShop, wallet: Wallet) {
    val currentSword = swordShop.currentSword
    val nextSword = swordShop.nextSword

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Forge d'Épées") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    CoinsView(wallet = wallet, modifier = Modifier.padding(end = 16.dp))
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Ton Épée Actuelle",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Current Sword Display
            Card(
                modifier = Modifier.size(200.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    if (currentSword.image != 0) {
                        Image(
                            painter = painterResource(id = currentSword.image),
                            contentDescription = currentSword.swordSrc,
                            modifier = Modifier.size(150.dp)
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${currentSword.material.icon} ${currentSword.material.label}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Grade ${currentSword.material.grade}",
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Divider()

            if (nextSword != null) {
                Button(
                    onClick = { swordShop.upgradeSword(wallet) },
                    enabled = wallet.balance >= nextSword.price,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Améliorer l'épée")
                }
            } else {
                Text(
                    text = "Tu possèdes l'épée ultime",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
