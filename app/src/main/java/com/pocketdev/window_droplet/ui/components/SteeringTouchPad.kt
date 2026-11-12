package com.pocketdev.window_droplet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SteeringTouchPad(
    onDirectTouchX: (x: Float) -> Unit,
    onSteer: (deltaX: Float, deltaY: Float) -> Unit,
    onTouchReleased: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        onDirectTouchX(offset.x)
                    },
                    onDragEnd = { onTouchReleased() },
                    onDragCancel = { onTouchReleased() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDirectTouchX(change.position.x)
                        onSteer(dragAmount.x, dragAmount.y)
                    }
                )
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0x59000000))
                .padding(horizontal = 24.dp, vertical = 9.dp)
        ) {
            Text(
                text = "◄ স্পর্শ করে ডানে-বামে সরান • পানির ফোঁটা দ্রুত অনুসরণ করবে ►",
                color = Color(0xD9FFFFFF),
                fontSize = 12.sp
            )
        }
    }
}
