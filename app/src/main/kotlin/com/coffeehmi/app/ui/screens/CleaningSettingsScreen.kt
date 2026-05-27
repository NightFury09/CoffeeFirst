package com.coffeehmi.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffeehmi.app.model.InventoryManager
import com.coffeehmi.app.ui.theme.DeepEspresso
import com.coffeehmi.app.ui.theme.Gold
import com.coffeehmi.app.ui.theme.ErrorRose
import kotlinx.coroutines.delay
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CleaningSettingsScreen(
    onBack: () -> Unit
) {
    // Collect settings from InventoryManager
    val initMilkOn by InventoryManager.milkCleanOnTime.collectAsState()
    val initMilkOff by InventoryManager.milkCleanOffTime.collectAsState()
    val initMilkCycles by InventoryManager.milkCleanCycleCount.collectAsState()
    val initMilkAuto by InventoryManager.autoMilkCleanTime.collectAsState()

    val initBrewerOn by InventoryManager.brewerCleanOnTime.collectAsState()
    val initBrewerCycles by InventoryManager.brewerCleanCycle.collectAsState()
    val initBrewerAuto by InventoryManager.autoBrewerCleanTime.collectAsState()

    // Screen states
    var milkOn by remember(initMilkOn) { mutableStateOf(initMilkOn) }
    var milkOff by remember(initMilkOff) { mutableStateOf(initMilkOff) }
    var milkCycles by remember(initMilkCycles) { mutableStateOf(initMilkCycles) }
    var milkAuto by remember(initMilkAuto) { mutableStateOf(initMilkAuto) }

    var brewerOn by remember(initBrewerOn) { mutableStateOf(initBrewerOn) }
    var brewerCycles by remember(initBrewerCycles) { mutableStateOf(initBrewerCycles) }
    var brewerAuto by remember(initBrewerAuto) { mutableStateOf(initBrewerAuto) }

    var saveToastShow by remember { mutableStateOf(false) }
    var isCleaningActive by remember { mutableStateOf(false) }

    LaunchedEffect(saveToastShow) {
        if (saveToastShow) {
            delay(2000)
            saveToastShow = false
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepEspresso)
    ) {
        val w = maxWidth
        val h = maxHeight

        val topBarH = (h.value * 0.12f).coerceIn(48f, 72f).dp
        val contentH = h - topBarH

        val outerPad = (w.value * 0.02f).coerceIn(8f, 20f).dp
        val colGap = (w.value * 0.02f).coerceIn(8f, 16f).dp

        val titleSize = (topBarH.value * 0.35f).coerceIn(16f, 24f).sp
        val labelSize = (h.value * 0.04f).coerceIn(10f, 16f).sp

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topBarH)
                    .padding(horizontal = outerPad),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CleaningServices,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(titleSize.value.dp * 1.1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CLEANING SETTINGS",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = titleSize,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.weight(1f)
                )

                // Top right status indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(topBarH * 0.65f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x1AFFFFFF))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Tune, null, tint = Gold, modifier = Modifier.size(topBarH * 0.32f))
                    }
                    Box(
                        modifier = Modifier
                            .size(topBarH * 0.65f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x1AFFFFFF))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Thermostat, null, tint = Gold, modifier = Modifier.size(topBarH * 0.32f))
                    }
                }
            }

            Divider(color = Color.White.copy(alpha = 0.15f), thickness = 1.dp)

            // Content split-pane cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(outerPad),
                horizontalArrangement = Arrangement.spacedBy(colGap)
            ) {
                // Left Column: Milk Cleaning Settings
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(12.dp)),
                    color = Color(0x0AFFFFFF),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "MILK SYSTEM CLEANING",
                            color = Gold,
                            fontWeight = FontWeight.Bold,
                            fontSize = labelSize * 1.1f,
                            letterSpacing = 1.sp
                        )

                        Divider(color = Color.White.copy(alpha = 0.08f))

                        CleaningSliderRow(
                            label = "Milk Clean ON",
                            value = milkOn,
                            onValueChange = { milkOn = it },
                            range = 0.5f..5.0f,
                            unit = "s",
                            labelSize = labelSize,
                            enabled = !isCleaningActive
                        )

                        CleaningSliderRow(
                            label = "Milk Clean OFF",
                            value = milkOff,
                            onValueChange = { milkOff = it },
                            range = 0.5f..5.0f,
                            unit = "s",
                            labelSize = labelSize,
                            enabled = !isCleaningActive
                        )

                        CleaningIntRow(
                            label = "Milk Clean Cycles",
                            value = milkCycles,
                            onValueChange = { milkCycles = it },
                            range = 1..10,
                            labelSize = labelSize,
                            enabled = !isCleaningActive
                        )

                        CleaningIntRow(
                            label = "Auto Milk Clean",
                            value = milkAuto,
                            onValueChange = { milkAuto = it },
                            range = 10..120,
                            unit = "s",
                            step = 5,
                            labelSize = labelSize,
                            enabled = !isCleaningActive
                        )
                    }
                }

                // Right Column: Brewer Cleaning Settings
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(12.dp)),
                    color = Color(0x0AFFFFFF),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "BREWER SYSTEM CLEANING",
                            color = Gold,
                            fontWeight = FontWeight.Bold,
                            fontSize = labelSize * 1.1f,
                            letterSpacing = 1.sp
                        )

                        Divider(color = Color.White.copy(alpha = 0.08f))

                        CleaningSliderRow(
                            label = "Brewer Clean ON",
                            value = brewerOn,
                            onValueChange = { brewerOn = it },
                            range = 1.0f..15.0f,
                            unit = "s",
                            labelSize = labelSize,
                            enabled = !isCleaningActive
                        )

                        CleaningIntRow(
                            label = "Brewer Cycles",
                            value = brewerCycles,
                            onValueChange = { brewerCycles = it },
                            range = 1..10,
                            labelSize = labelSize,
                            enabled = !isCleaningActive
                        )

                        CleaningIntRow(
                            label = "Auto Brewer Clean",
                            value = brewerAuto,
                            onValueChange = { brewerAuto = it },
                            range = 10..120,
                            unit = "s",
                            step = 5,
                            labelSize = labelSize,
                            enabled = !isCleaningActive
                        )

                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Note: Brewer cleaning cycle consumes hot water. Ensure drip tray is empty and container is placed under outlets.",
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = labelSize * 0.75f,
                            lineHeight = (labelSize.value * 1.1f).sp
                        )
                    }
                }
            }

            // Bottom Actions: BACK, RUN CLEANING, & SAVE (Hidden during cleaning)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = (h.value * 0.015f).coerceIn(4f, 12f).dp, end = outerPad, start = outerPad),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // BACK button
                Button(
                    onClick = onBack,
                    enabled = !isCleaningActive,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCleaningActive) Color(0x0DFFFFFF) else Color(0x1EFFFFFF)
                    ),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text("BACK", color = if (isCleaningActive) Color.White.copy(alpha = 0.3f) else Color.White, fontWeight = FontWeight.Bold, fontSize = labelSize * 0.9f)
                }

                if (!isCleaningActive) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // RUN CLEANING button
                        Button(
                            onClick = { isCleaningActive = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = DeepEspresso),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                        ) {
                            Text("RUN CLEANING", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        // SAVE button
                        Button(
                            onClick = {
                                InventoryManager.saveCleaningSettings(
                                    milkOn, milkOff, milkCycles, milkAuto,
                                    brewerOn, brewerCycles, brewerAuto
                                )
                                saveToastShow = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                        ) {
                            Text("SAVE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }
            }
        }

        // Self Cleaning Overlay: rendered at the root Box level to completely cover the screen when active.
        if (isCleaningActive) {
            SelfCleaningOverlay(
                onDismiss = {
                    isCleaningActive = false
                    InventoryManager.addLog("Self-cleaning process finished.")
                }
            )
        }

        // Overlay Notification for saved settings
        if (saveToastShow) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
                    .background(Color(0xE62E7D32), RoundedCornerShape(6.dp))
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Cleaning parameters saved successfully!",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun SelfCleaningOverlay(onDismiss: () -> Unit) {
    var progress by remember { mutableStateOf(0f) }
    var phaseText by remember { mutableStateOf("Initializing...") }
    var flowRate by remember { mutableStateOf(0f) }
    var pressureVal by remember { mutableStateOf(0f) }
    var tempVal by remember { mutableStateOf(25f) }

    // Increment progress and update diagnostic telemetry dynamically
    LaunchedEffect(Unit) {
        phaseText = "Flushing Milk System Circuits..."
        flowRate = 8.5f; pressureVal = 2.4f; tempVal = 65f
        delay(2000); progress = 0.25f
        
        phaseText = "Injecting High-Temp Descaling Agent..."
        flowRate = 3.2f; pressureVal = 15.0f; tempVal = 95f
        delay(2500); progress = 0.50f
        
        phaseText = "Steam Wand Purging & Pressure Flush..."
        flowRate = 12.0f; pressureVal = 8.2f; tempVal = 105f
        delay(2500); progress = 0.75f
        
        phaseText = "Finalizing Water Chamber Rinsing..."
        flowRate = 10.1f; pressureVal = 4.0f; tempVal = 88f
        delay(2000); progress = 1.0f
        
        flowRate = 0f; pressureVal = 0f; tempVal = 45f
        delay(1000)
        onDismiss()
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "cleaning_progress"
    )

    // Infinitely running rotation/flow offsets
    val infiniteTransition = rememberInfiniteTransition(label = "flow_pulses")
    val flowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flow"
    )
    val rotateAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.96f))
            .clickable(enabled = false) {}, // absorb clicks to prevent underlying actions
        contentAlignment = Alignment.Center
    ) {
        val overlayW = maxWidth
        val overlayH = maxHeight
        val panelW = (overlayW.value * 0.90f).dp
        val canvasW = (overlayW.value * 0.55f).dp
        val telemetryW = (overlayW.value * 0.30f).dp
        val canvasH = (overlayH.value * 0.48f).dp
        val titleSp = (overlayH.value * 0.045f).coerceIn(14f, 22f).sp
        val bodyPad = (overlayW.value * 0.03f).coerceIn(8f, 24f).dp

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = bodyPad, vertical = (overlayH.value * 0.03f).coerceIn(8f, 24f).dp)
        ) {
            Text(
                text = "SYSTEM SELF-CLEANING CYCLE",
                color = Gold,
                fontWeight = FontWeight.Bold,
                fontSize = titleSp,
                letterSpacing = 2.sp
            )

            // Dynamic Main Pane: Schematic + Telemetry HUD Side-by-Side
            Row(
                modifier = Modifier
                    .width(panelW)
                    .height(canvasH),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Part: High-tech Schematic Canvas
                Box(
                    modifier = Modifier
                        .weight(1.8f)
                        .fillMaxHeight()
                        .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x0F8D6E63), Color(0x05000000))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Coordinates for Nodes
                        val resCenter = Offset(w * 0.16f, h * 0.40f)
                        val pumpCenter = Offset(w * 0.42f, h * 0.40f)
                        val boilerCenter = Offset(w * 0.68f, h * 0.40f)
                        val outletCenter = Offset(w * 0.86f, h * 0.70f)

                        // 1. Draw connecting glass tube lines (translucent background tubes)
                        drawTube(resCenter, pumpCenter)
                        drawTube(pumpCenter, boilerCenter)
                        drawTube(boilerCenter, outletCenter)

                        // 2. Determine fluid color by phase
                        val fluidColor = when {
                            animatedProgress < 0.25f -> Color(0xFFF5F5DC) // Milky White (milk system flush)
                            animatedProgress < 0.50f -> Color(0xFFFFB74D) // Amber / Chemical Descaler
                            animatedProgress < 0.75f -> Color(0xFFE0F7FA).copy(alpha = 0.5f) // Steam mist
                            else -> Color(0xFF4FC3F7) // Pure water blue
                        }

                        // 3. Draw active flowing fluid pulses inside the tubes
                        drawFluidFlow(resCenter, pumpCenter, flowOffset, fluidColor)
                        drawFluidFlow(pumpCenter, boilerCenter, (flowOffset + 33f) % 100f, fluidColor)
                        drawFluidFlow(boilerCenter, outletCenter, (flowOffset + 66f) % 100f, fluidColor)

                        // 4. Reservoir Node (shows water level lowering)
                        drawReservoir(resCenter, animatedProgress)

                        // 5. Pump Node (with rotating impeller blades)
                        drawPump(pumpCenter, rotateAngle)

                        // 6. Boiler Heater Node (pulses red/orange)
                        drawBoiler(boilerCenter, flowOffset)

                        // 7. Spray/Steam droplet particles at outlet nozzle
                        if (animatedProgress > 0f && animatedProgress < 1.0f) {
                            drawOutletSpray(outletCenter, flowOffset, fluidColor)
                        }
                    }

                    // Floating text labels overlaying the Canvas
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text("RESERVOIR", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.TopStart).padding(start = 22.dp, top = 22.dp))
                        Text("FLOW PUMP", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.TopCenter).padding(end = 40.dp, top = 22.dp))
                        Text("THERMO BLOCK", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.TopEnd).padding(end = 65.dp, top = 22.dp))
                        Text("NOZZLE", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.BottomEnd).padding(end = 40.dp, bottom = 44.dp))
                    }
                }

                // Right Part: High-Tech Telemetry HUD Panel
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(16.dp)),
                    color = Color(0x08FFFFFF),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            "DIAGNOSTICS HUD",
                            color = Gold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        
                        Divider(color = Color.White.copy(alpha = 0.1f))

                        TelemetryItem(
                            label = "SYSTEM TEMP",
                            value = String.format(java.util.Locale.US, "%.1f °C", tempVal),
                            subText = if (tempVal > 90) "HEATING ACTIVE" else "STABLE"
                        )
                        TelemetryItem(
                            label = "HYDRAULIC PRESSURE",
                            value = String.format(java.util.Locale.US, "%.1f Bar", pressureVal),
                            subText = if (pressureVal > 12) "HIGH PRESSURE" else "NOMINAL"
                        )
                        TelemetryItem(
                            label = "FLOW RATE",
                            value = String.format(java.util.Locale.US, "%.1f ml/s", flowRate),
                            subText = if (flowRate > 0) "CIRCULATION OK" else "IDLE"
                        )
                    }
                }
            }

            Text(
                text = phaseText,
                color = Color.White,
                fontSize = (overlayH.value * 0.035f).coerceIn(12f, 17f).sp,
                fontWeight = FontWeight.SemiBold
            )

            // Progress bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(0.75f)
            ) {
                LinearProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Gold,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    color = Gold,
                    fontWeight = FontWeight.Bold,
                    fontSize = (overlayH.value * 0.035f).coerceIn(12f, 17f).sp
                )
            }

            // Halt button
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRose),
                shape = RoundedCornerShape(4.dp),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 10.dp)
            ) {
                Text("HALT CYCLE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

// ── Draw Helper Methods for schematic ────────────────────────────────────────

private fun DrawScope.drawTube(start: Offset, end: Offset) {
    drawLine(
        color = Color(0x1AFFFFFF),
        start = start,
        end = end,
        strokeWidth = 10.dp.toPx(),
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color(0x33000000),
        start = start,
        end = end,
        strokeWidth = 8.dp.toPx(),
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawFluidFlow(
    start: Offset,
    end: Offset,
    flowOffset: Float,
    fluidColor: Color
) {
    val totalDx = end.x - start.x
    val totalDy = end.y - start.y
    val sizePx = 6.dp.toPx()

    // Render 3 flowing beads along the path
    for (i in 0..2) {
        val fraction = ((flowOffset + i * 33.3f) % 100f) / 100f
        val x = start.x + totalDx * fraction
        val y = start.y + totalDy * fraction
        drawCircle(
            color = fluidColor,
            radius = sizePx,
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.drawReservoir(center: Offset, progress: Float) {
    val r = 24.dp.toPx()
    // Outer glass housing
    drawCircle(
        color = Color.White.copy(alpha = 0.2f),
        radius = r,
        center = center,
        style = Stroke(width = 2.dp.toPx())
    )
    drawCircle(
        color = Color(0x0FFFFFFF),
        radius = r - 1.dp.toPx(),
        center = center
    )
    
    // Liquid level inside reservoir (decreases as progress increases)
    val liquidPct = (1f - progress * 0.7f).coerceIn(0.2f, 1f)
    val clipPath = Path().apply {
        val startY = center.y + r - (r * 2 * liquidPct)
        moveTo(center.x - r, startY)
        lineTo(center.x + r, startY)
        arcTo(
            rect = Rect(center.x - r, center.y - r, center.x + r, center.y + r),
            startAngleDegrees = 0f,
            sweepAngleDegrees = 180f,
            forceMoveTo = false
        )
        close()
    }
    drawPath(
        path = clipPath,
        brush = Brush.verticalGradient(listOf(Color(0xFF29B6F6), Color(0xFF0288D1)))
    )
}

private fun DrawScope.drawPump(center: Offset, rotateAngle: Float) {
    val r = 18.dp.toPx()
    // Pump Housing
    drawCircle(
        color = Color.White.copy(alpha = 0.3f),
        radius = r,
        center = center,
        style = Stroke(width = 2.dp.toPx())
    )
    drawCircle(
        color = Color(0x26FFFFFF),
        radius = r - 1.dp.toPx(),
        center = center
    )

    // Spinning pump impeller blades
    val angleRad = Math.toRadians(rotateAngle.toDouble())
    for (i in 0..2) {
        val bladeAngle = angleRad + i * (2 * Math.PI / 3)
        val endPoint = Offset(
            (center.x + (r - 4.dp.toPx()) * cos(bladeAngle)).toFloat(),
            (center.y + (r - 4.dp.toPx()) * sin(bladeAngle)).toFloat()
        )
        drawLine(
            color = Gold,
            start = center,
            end = endPoint,
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
    drawCircle(
        color = Gold,
        radius = 4.dp.toPx(),
        center = center
    )
}

private fun DrawScope.drawBoiler(center: Offset, flowOffset: Float) {
    val r = 26.dp.toPx()
    // Pulsing heating core glow (sine wave based on flowOffset)
    val pulse = 0.3f + 0.5f * sin(flowOffset * Math.PI.toFloat() / 50f)
    drawCircle(
        color = Color(0xFFFF5252).copy(alpha = (pulse * 0.25f).coerceIn(0f, 1f)),
        radius = r + 8.dp.toPx(),
        center = center
    )
    
    // Core housing
    drawCircle(
        color = Color.White.copy(alpha = 0.2f),
        radius = r,
        center = center,
        style = Stroke(width = 2.dp.toPx())
    )
    
    // Heating element coil representation
    drawCircle(
        color = Color(0xFFFF5252),
        radius = r - 6.dp.toPx(),
        center = center,
        style = Stroke(width = 3.dp.toPx())
    )
    drawCircle(
        color = Color(0xFFFFB74D),
        radius = r - 14.dp.toPx(),
        center = center,
        style = Stroke(width = 2.dp.toPx())
    )
}

private fun DrawScope.drawOutletSpray(
    center: Offset,
    flowOffset: Float,
    fluidColor: Color
) {
    // Render nozzle tip
    drawRect(
        color = Color.White.copy(alpha = 0.5f),
        topLeft = Offset(center.x - 6.dp.toPx(), center.y - 12.dp.toPx()),
        size = Size(12.dp.toPx(), 12.dp.toPx())
    )

    // Render animated droplets shooting downwards
    for (i in 0..5) {
        val fraction = ((flowOffset + i * 20f) % 100f) / 100f
        val distance = 60.dp.toPx()
        val dy = fraction * distance
        val dx = (i - 2.5f) * 6.dp.toPx() * fraction
        drawCircle(
            color = fluidColor.copy(alpha = ((1f - fraction) * 0.8f).coerceIn(0f, 1f)),
            radius = (3.dp.toPx() * (1f - fraction * 0.5f)),
            center = Offset(center.x + dx, center.y + dy)
        )
    }

    // Render rising steam clouds
    for (i in 0..2) {
        val fraction = ((flowOffset + i * 35f) % 100f) / 100f
        val sy = -fraction * 35.dp.toPx()
        val sx = sin((flowOffset + i * 40f) * Math.PI.toFloat() / 50f) * 8.dp.toPx()
        drawCircle(
            color = Color.White.copy(alpha = ((1f - fraction) * 0.15f).coerceIn(0f, 1f)),
            radius = 12.dp.toPx() + fraction * 10.dp.toPx(),
            center = Offset(center.x + sx, center.y + sy)
        )
    }
}

@Composable
private fun TelemetryItem(label: String, value: String, subText: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(subText, color = Gold.copy(alpha = 0.6f), fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun CleaningSliderRow(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    unit: String = "",
    labelSize: androidx.compose.ui.unit.TextUnit,
    enabled: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = if (enabled) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.3f),
                fontWeight = FontWeight.SemiBold,
                fontSize = labelSize * 0.9f
            )
            Text(
                text = String.format("%.1f%s", value, unit),
                color = if (enabled) Gold else Color.White.copy(alpha = 0.3f),
                fontWeight = FontWeight.Bold,
                fontSize = labelSize
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "-",
                color = if (enabled) Color.White.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.2f),
                fontSize = 20.sp,
                modifier = Modifier
                    .width(24.dp)
                    .clickable(enabled = enabled) {
                        onValueChange((value - 0.1f).coerceIn(range.start, range.endInclusive))
                    },
                textAlign = TextAlign.Center
            )
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = range,
                enabled = enabled,
                colors = SliderDefaults.colors(
                    thumbColor = Gold,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.White.copy(alpha = 0.2f),
                    disabledThumbColor = Color.White.copy(alpha = 0.3f),
                    disabledActiveTrackColor = Color.White.copy(alpha = 0.1f),
                    disabledInactiveTrackColor = Color.White.copy(alpha = 0.1f)
                ),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "+",
                color = if (enabled) Color.White.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.2f),
                fontSize = 20.sp,
                modifier = Modifier
                    .width(24.dp)
                    .clickable(enabled = enabled) {
                        onValueChange((value + 0.1f).coerceIn(range.start, range.endInclusive))
                    },
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CleaningIntRow(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    unit: String = "",
    step: Int = 1,
    labelSize: androidx.compose.ui.unit.TextUnit,
    enabled: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = if (enabled) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.3f),
                fontWeight = FontWeight.SemiBold,
                fontSize = labelSize * 0.9f
            )
            Text(
                text = "$value$unit",
                color = if (enabled) Gold else Color.White.copy(alpha = 0.3f),
                fontWeight = FontWeight.Bold,
                fontSize = labelSize
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "-",
                color = if (enabled) Color.White.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.2f),
                fontSize = 20.sp,
                modifier = Modifier
                    .width(24.dp)
                    .clickable(enabled = enabled) {
                        onValueChange((value - step).coerceIn(range.first, range.last))
                    },
                textAlign = TextAlign.Center
            )
            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = range.first.toFloat()..range.last.toFloat(),
                steps = if (step == 1) (range.last - range.first - 1) else ((range.last - range.first) / step - 1),
                enabled = enabled,
                colors = SliderDefaults.colors(
                    thumbColor = Gold,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.White.copy(alpha = 0.2f),
                    disabledThumbColor = Color.White.copy(alpha = 0.3f),
                    disabledActiveTrackColor = Color.White.copy(alpha = 0.1f),
                    disabledInactiveTrackColor = Color.White.copy(alpha = 0.1f)
                ),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "+",
                color = if (enabled) Color.White.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.2f),
                fontSize = 20.sp,
                modifier = Modifier
                    .width(24.dp)
                    .clickable(enabled = enabled) {
                        onValueChange((value + step).coerceIn(range.first, range.last))
                    },
                textAlign = TextAlign.Center
            )
        }
    }
}
