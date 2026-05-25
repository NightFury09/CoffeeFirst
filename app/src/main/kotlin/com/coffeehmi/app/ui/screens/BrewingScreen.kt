package com.coffeehmi.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffeehmi.app.model.InventoryManager
import com.coffeehmi.app.model.beverageCatalog
import com.coffeehmi.app.ui.theme.DeepEspresso
import com.coffeehmi.app.ui.theme.Espresso
import com.coffeehmi.app.ui.theme.Gold
import com.coffeehmi.app.ui.theme.GoldGlowing
import com.coffeehmi.app.ui.theme.Latte
import com.coffeehmi.app.ui.theme.ErrorRose
import kotlinx.coroutines.delay

@Composable
fun BrewingScreen(
    beverageId: String?,
    onComplete: () -> Unit,
    onStop: () -> Unit
) {
    val beverage = beverageCatalog.find { it.id == beverageId }
    var progress   by remember { mutableStateOf(0f) }
    var statusText by remember { mutableStateOf("Initializing...") }
    val canBrew = remember(beverageId) { InventoryManager.canBrew(beverageId) }

    val animatedProgress by animateFloatAsState(
        targetValue    = progress,
        animationSpec  = androidx.compose.animation.core.tween(durationMillis = 1000),
        label          = "BrewingProgress"
    )

    LaunchedEffect(Unit) {
        if (!canBrew) {
            statusText = "Error: Out of ingredients!"
            return@LaunchedEffect
        }
        statusText = "Heating water..."
        delay(2000); progress = 0.2f

        statusText = "Grinding ${beverage?.name} beans..."
        delay(3000); progress = 0.5f

        statusText = "Extracting rich flavors..."
        delay(5000); progress = 0.8f

        statusText = "Finalizing your perfect cup..."
        delay(2000); progress = 1.0f

        delay(1500)
        
        // Successful brew reduces ingredients
        InventoryManager.brew(beverageId)
        
        onComplete()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepEspresso),
        contentAlignment = Alignment.Center
    ) {
        val w = maxWidth
        val h = maxHeight

        val outerPad   = (w.value * 0.02f).coerceIn(8f, 24f).dp
        val colGap     = (w.value * 0.03f).coerceIn(12f, 32f).dp

        val ringSize: Dp    = (h.value * 0.50f).coerceIn(140f, 280f).dp
        val cupSize: Dp     = (ringSize.value * 0.40f).coerceIn(50f, 110f).dp
        val statusFontSize  = (h.value * 0.050f).coerceIn(12f, 24f).sp
        val pctFontSize     = (h.value * 0.035f).coerceIn(10f, 18f).sp
        val titleFontSize   = (h.value * 0.070f).coerceIn(18f, 32f).sp
        val descFontSize    = (h.value * 0.040f).coerceIn(10f, 18f).sp
        val quoteFontSize   = (h.value * 0.038f).coerceIn(9f, 16f).sp

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(outerPad),
            horizontalArrangement = Arrangement.spacedBy(colGap),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Side: Beverage Card & Info
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x0DFFFFFF))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    beverage?.let {
                        Image(
                            painter = painterResource(id = it.imageRes),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            alpha = 0.35f
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = beverage?.name ?: "Brewing",
                                fontSize = titleFontSize,
                                fontWeight = FontWeight.Bold,
                                color = Gold
                            )
                            Text(
                                text = beverage?.description ?: "",
                                fontSize = descFontSize,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0x1AFFFFFF), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "\"${beverage?.quote ?: ""}\"",
                                fontSize = quoteFontSize,
                                fontWeight = FontWeight.Light,
                                color = Latte,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                lineHeight = (quoteFontSize.value * 1.4f).sp
                            )
                        }
                    }
                }
            }

            // Right Side: Progress animation & STOP button
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(ringSize)
                ) {
                    // Glowing Progress Ring
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = (size.minDimension * 0.025f)
                        drawArc(
                            color      = Gold.copy(alpha = 0.1f),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter  = false,
                            style      = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        drawArc(
                            brush      = Brush.sweepGradient(listOf(Gold, GoldGlowing, Gold)),
                            startAngle = -90f,
                            sweepAngle = animatedProgress * 360f,
                            useCenter  = false,
                            style      = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    // Filling Cup
                    FillingCup(progress = animatedProgress, size = cupSize)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text       = statusText,
                    fontSize   = statusFontSize,
                    color      = if (canBrew) Gold else ErrorRose,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text     = "${(animatedProgress * 100).toInt()}%",
                    fontSize = pctFontSize,
                    color    = Gold.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onStop,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canBrew) ErrorRose.copy(alpha = 0.85f) else Gold,
                        contentColor = if (canBrew) Color.White else DeepEspresso
                    ),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = if (canBrew) "STOP" else "BACK",
                        fontWeight = FontWeight.Bold,
                        fontSize = (statusFontSize.value * 0.8f).sp,
                        letterSpacing = 1.2.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FillingCup(progress: Float, size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val width  = this.size.width
        val height = this.size.height

        val cupPath = Path().apply {
            moveTo(width * 0.2f, height * 0.2f)
            lineTo(width * 0.8f, height * 0.2f)
            lineTo(width * 0.7f, height * 0.9f)
            lineTo(width * 0.3f, height * 0.9f)
            close()
        }

        drawPath(
            path  = cupPath,
            color = Color.White.copy(alpha = 0.2f),
            style = Stroke(width = (width * 0.025f), cap = StrokeCap.Round)
        )

        if (progress > 0) {
            val actualFillPath = Path().apply {
                val currentY        = height * (0.9f - 0.7f * progress)
                val topWidthFactor  = 0.2f + (0.1f * (1f - progress))

                moveTo(width * 0.3f, height * 0.9f)
                lineTo(width * 0.7f, height * 0.9f)
                lineTo(width * (1f - topWidthFactor), currentY)
                lineTo(width * topWidthFactor, currentY)
                close()
            }
            drawPath(
                path  = actualFillPath,
                brush = Brush.verticalGradient(listOf(Color(0xFF6F4E37), Espresso))
            )
        }
    }
}
