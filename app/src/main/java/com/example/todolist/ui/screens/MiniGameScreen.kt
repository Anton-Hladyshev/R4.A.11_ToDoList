package com.example.todolist.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todolist.R
import com.example.todolist.controller.MonsterManager
import com.example.todolist.model.*
import com.example.todolist.ui.components.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniGameScreen(
    navController: NavController,
    level: Level,
    swordShop: SwordShop,
    wallet: Wallet,
    monsterManager: MonsterManager,
    showScaffold: Boolean = true
) {
    val context = LocalContext.current
    
    var currentMonster by remember { mutableStateOf<Monster?>(null) }
    var currentFrameIndex by remember { mutableIntStateOf(0) }
    
    val swordRotation = remember { Animatable(0f) }
    val swordTranslationY = remember { Animatable(0f) }
    val swordScale = remember { Animatable(1f) }
    
    val monsterAlpha = remember { Animatable(1f) }
    val monsterScale = remember { Animatable(1f) }
    
    val totalAnimationTime = 500L

    // Load initial monster
    LaunchedEffect(level.currentLevel) {
        if (currentMonster == null || currentMonster?.data?.level != level.currentLevel) {
            currentMonster = monsterManager.getRandomMonsterForLevel(level.currentLevel)
        }
    }

    // Animation & Combat Loop
    LaunchedEffect(currentMonster) {
        val monster = currentMonster ?: return@LaunchedEffect
        
        // Appearance animation
        monsterAlpha.snapTo(0f)
        monsterScale.snapTo(0.5f)
        coroutineScope {
            launch { monsterAlpha.animateTo(1f, tween(300)) }
            launch { monsterScale.animateTo(1f, tween(300, easing = { x ->
                val t = x - 1f
                t * t * ((2f + 1f) * t + 2f) + 1f
            })) }
        }
        
        val frameDuration = totalAnimationTime / monster.frames.size
        
        while (isActive && !monster.isDead()) {
            coroutineScope {
                // Sword attack
                launch {
                    swordRotation.animateTo(90f, animationSpec = tween(150, easing = FastOutSlowInEasing))
                    monster.takeDamage(swordShop.currentSword.damage)
                    swordRotation.animateTo(0f, animationSpec = tween(300))
                }
                launch {
                    swordTranslationY.animateTo(40f, animationSpec = tween(150, easing = FastOutSlowInEasing))
                    swordTranslationY.animateTo(0f, animationSpec = tween(300))
                }
                launch {
                    swordScale.animateTo(1.2f, animationSpec = tween(150))
                    swordScale.animateTo(1f, animationSpec = tween(300))
                }

                launch {
                    for (i in monster.frames.indices) {
                        currentFrameIndex = i
                        delay(frameDuration)
                    }
                }
            }
            
            if (monster.isDead()) {
                coroutineScope {
                    launch { monsterAlpha.animateTo(0f, tween(400)) }
                    launch { monsterScale.animateTo(1.5f, tween(400)) }
                }
                level.addXp(monster.data.xpValue)
                delay(200)
                currentMonster = monsterManager.getRandomMonsterForLevel(level.currentLevel)
            } else {
                delay(300)
            }
        }
    }

    // Load background from assets
    val backgroundBitmap = remember(level.currentLevel) {
        try {
            val assetPath = "backgrounds/${level.getBackgroundResourceName()}.png"
            val inputStream = context.assets.open(assetPath)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }

    val content = @Composable { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                // Background Image - Aligned to bottom to keep the floor consistent
                if (backgroundBitmap != null) {
                    Image(
                        bitmap = backgroundBitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.BottomCenter
                    )
                } else {
                    val fallbackResId = context.resources.getIdentifier(
                        level.getBackgroundResourceName(),
                        "drawable",
                        context.packageName
                    )
                    if (fallbackResId != 0) {
                        Image(
                            painter = painterResource(id = fallbackResId),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.BottomCenter
                        )
                    }
                }

                // Game Scene
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Monster - Placed relative to the bottom
                    currentMonster?.let { m ->
                        MonsterComponent(
                            monster = m,
                            currentFrameIndex = currentFrameIndex,
                            alpha = monsterAlpha.value,
                            scale = monsterScale.value,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                // Adjusting Y to match the floor in cropped background
                                .offset(x = 60.dp, y = (-64).dp)
                        )
                    }

                    // Sword - Aligned with the monster
                    SwordComponent(
                        sword = swordShop.currentSword,
                        rotation = swordRotation.value,
                        translationY = swordTranslationY.value,
                        scale = swordScale.value,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 50.dp, bottom = 100.dp)
                    )
                }
                
                // Top UI with Custom Progress Bars
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                        .width(240.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.level_label, level.currentLevel),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.3f), shape = MaterialTheme.shapes.medium)
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    CustomProgressBar(
                        currentValue = level.currentXp,
                        maxValue = level.getLevelMaxXp(),
                        color = Color(0xFF4CAF50),
                        label = stringResource(id = R.string.xp_progress_label),
                        height = 24.dp
                    )
                }
            }

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
                    title = { Text(stringResource(id = R.string.mini_game_title)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
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
