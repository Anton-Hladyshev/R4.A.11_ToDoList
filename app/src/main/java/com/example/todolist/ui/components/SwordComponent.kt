package com.example.todolist.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.todolist.model.Sword

@Composable
fun SwordComponent(
    sword: Sword,
    rotation: Float,
    translationY: Float,
    scale: Float,
    modifier: Modifier = Modifier
) {
    if (sword.bitmap != null) {
        Image(
            bitmap = sword.bitmap.asImageBitmap(),
            contentDescription = "Sword",
            modifier = modifier
                .size(120.dp)
                .graphicsLayer {
                    rotationZ = rotation
                    this.translationY = translationY
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin(0.2f, 0.8f)
                }
        )
    } else if (sword.image != 0) {
        // Fallback to resource if bitmap failed to load and resource ID is valid
        Image(
            painter = painterResource(id = sword.image),
            contentDescription = "Sword",
            modifier = modifier
                .size(120.dp)
                .graphicsLayer {
                    rotationZ = rotation
                    this.translationY = translationY
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin(0.2f, 0.8f)
                }
        )
    }
}
