package com.example.todolist.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todolist.model.Level
import com.example.todolist.model.SwordShop
import com.example.todolist.model.Wallet
import com.example.todolist.ui.components.SwordUpgradeComponent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniGameScreen(
    navController: NavController,
    level: Level,
    swordShop: SwordShop,
    wallet: Wallet,
    showScaffold: Boolean = true
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val swordRotation = remember { Animatable(0f) }
    val swordTranslationY = remember { Animatable(0f) }
    val swordScale = remember { Animatable(1f) }

    val backgroundResId = context.resources.getIdentifier(
        level.getBackgroundResourceName(),
        "drawable",
        context.packageName
    )

    val content = @Composable { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Top section with background image and game area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        wallet.deposit(1)
                        scope.launch {
                            // Attack animation
                            launch {
                                swordRotation.animateTo(90f, animationSpec = tween(50, easing = FastOutSlowInEasing))
                                swordRotation.animateTo(0f, animationSpec = tween(150))
                            }
                            launch {
                                swordTranslationY.animateTo(40f, animationSpec = tween(50, easing = FastOutSlowInEasing))
                                swordTranslationY.animateTo(0f, animationSpec = tween(150))
                            }
                            launch {
                                swordScale.animateTo(1.2f, animationSpec = tween(50))
                                swordScale.animateTo(1f, animationSpec = tween(150))
                            }
                        }
                    }
            ) {
                // Background Image
                if (backgroundResId != 0) {
                    Image(
                        painter = painterResource(id = backgroundResId),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Animated Sword
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Image(
                        painter = painterResource(id = swordShop.currentSword.image),
                        contentDescription = "Sword",
                        modifier = Modifier
                            .padding(start = 50.dp, top = 100.dp)
                            .size(100.dp)
                            .graphicsLayer {
                                rotationZ = swordRotation.value
                                translationY = swordTranslationY.value
                                scaleX = swordScale.value
                                scaleY = swordScale.value
                                transformOrigin = TransformOrigin(0.2f, 0.8f)
                            }
                    )
                }
                
                // Overlay text for level
                Text(
                    text = "Niveau ${level.currentLevel}",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.3f), shape = MaterialTheme.shapes.medium)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    color = Color.White
                )
            }

            // Shop Component at the bottom
            SwordUpgradeComponent(
                swordShop = swordShop,
                wallet = wallet,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }

    if (showScaffold) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Mini Jeu") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White.copy(alpha = 0.5f)
                    )
                )
            }
        ) { innerPadding ->
            content(innerPadding)
        }
    } else {
        content(PaddingValues(0.dp))
    }
}
