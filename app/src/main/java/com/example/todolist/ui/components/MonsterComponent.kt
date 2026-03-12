package com.example.todolist.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.todolist.model.Monster

@Composable
fun MonsterComponent(
    monster: Monster,
    currentFrameIndex: Int,
    alpha: Float,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .graphicsLayer {
                this.alpha = alpha
                this.scaleX = scale
                this.scaleY = scale
                // Fix the pivot point to the bottom center so animations (spawn/death) 
                // happen relative to the ground.
                this.transformOrigin = TransformOrigin(0.5f, 1f)
            }
    ) {
        // Barre de vie du monstre
        CustomProgressBar(
            currentValue = monster.currentHp,
            maxValue = monster.data.maxHp,
            color = Color.Red,
            height = 12.dp,
            modifier = Modifier.width(120.dp),
            showText = false
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (currentFrameIndex < monster.frames.size) {
            Image(
                bitmap = monster.frames[currentFrameIndex].asImageBitmap(),
                contentDescription = "Monster",
                modifier = Modifier
                    .size(200.dp),
                alignment = Alignment.BottomCenter,
                contentScale = ContentScale.Fit
            )
        }
    }
}
