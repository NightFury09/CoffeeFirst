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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap

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
        val cardW = (w - outerPad * 2 - colGap) / 2

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
                            labelSize = labelSize
                        )

                        CleaningSliderRow(
                            label = "Milk Clean OFF",
                            value = milkOff,
                            onValueChange = { milkOff = it },
                            range = 0.5f..5.0f,
                            unit = "s",
                            labelSize = labelSize
                        )

                        CleaningIntRow(
                            label = "Milk Clean Cycles",
                            value = milkCycles,
                            onValueChange = { milkCycles = it },
                            range = 1..10,
                            labelSize = labelSize
                        )

                        CleaningIntRow(
                            label = "Auto Milk Clean",
                            value = milkAuto,
                            onValueChange = { milkAuto = it },
                            range = 10..120,
                            unit = "s",
                            step = 5,
                            labelSize = labelSize
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
                            labelSize = labelSize
                        )

                        CleaningIntRow(
                            label = "Brewer Cycles",
                            value = brewerCycles,
                            onValueChange = { brewerCycles = it },
                            range = 1..10,
                            labelSize = labelSize
                        )

                        CleaningIntRow(
                            label = "Auto Brewer Clean",
                            value = brewerAuto,
                            onValueChange = { brewerAuto = it },
                            range = 10..120,
                            unit = "s",
                            step = 5,
                            labelSize = labelSize
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

            // Bottom Actions: BACK, RUN CLEANING, & SAVE
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x1EFFFFFF)),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text("BACK", color = Color.White, fontWeight = FontWeight.Bold, fontSize = labelSize * 0.9f)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // RUN CLEANING button
                    var isCleaningActive by remember { mutableStateOf(false) }
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

                    if (isCleaningActive) {
                        SelfCleaningOverlay(
                            onDismiss = {
                                isCleaningActive = false
                                InventoryManager.addLog("Self-cleaning process finished.")
                            }
                        )
                    }
                }
            }
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

    // Increment progress and loop through phases
    LaunchedEffect(Unit) {
        phaseText = "Flushing Milk System Circuits..."
        delay(1500); progress = 0.25f
        phaseText = "Injecting High-Temp Descaling Agent..."
        delay(2000); progress = 0.50f
        phaseText = "Steam Wand Purging & Pressure Flush..."
        delay(2000); progress = 0.75f
        phaseText = "Finalizing Water Chamber Rinsing..."
        delay(1500); progress = 1.0f
        delay(1000)
        onDismiss()
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "cleaning_progress"
    )

    // Running particle/flow offsets
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

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
            .clickable(enabled = false) {}, // absorb clicks
        contentAlignment = Alignment.Center
    ) {
        val overlayW = maxWidth
        val overlayH = maxHeight
        val canvasW = (overlayW.value * 0.72f).coerceIn(260f, 520f).dp
        val canvasH = (overlayH.value * 0.32f).coerceIn(140f, 260f).dp
        val titleSp = (overlayH.value * 0.045f).coerceIn(14f, 22f).sp
        val bodyPad = (overlayW.value * 0.04f).coerceIn(12f, 36f).dp

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
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

            // Canvas Animation Area (Fluid path schematic) – fully responsive
            Box(
                modifier = Modifier
                    .size(canvasW, canvasH)
                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(12.dp))
                    .background(Color(0x05FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Draw static schematic nodes (Reservoir, Boiler, Valve, Outlet)
                    val resCenter = Offset(w * 0.15f, h * 0.35f)
                    val pumpCenter = Offset(w * 0.40f, h * 0.35f)
                    val boilerCenter = Offset(w * 0.65f, h * 0.35f)
                    val outletCenter = Offset(w * 0.85f, h * 0.70f)

                    // Draw lines connecting nodes
                    drawLine(Color(0x33FFFFFF), resCenter, pumpCenter, strokeWidth = 8.dp.toPx())
                    drawLine(Color(0x33FFFFFF), pumpCenter, boilerCenter, strokeWidth = 8.dp.toPx())
                    drawLine(Color(0x33FFFFFF), boilerCenter, outletCenter, strokeWidth = 8.dp.toPx())

                    // Draw flow pulses along lines
                    val dotColor = when {
                        animatedProgress < 0.3f -> Color.White // milk cleaning
                        animatedProgress < 0.7f -> Color(0xFFFFB74D) // chemical agent
                        else -> Color(0xFF2196F3) // water rinsing
                    }

                    // Animate flow dots moving along paths
                    val step1Dist = (pumpCenter.x - resCenter.x)
                    val dot1X = resCenter.x + ((flowOffset / 100f) * step1Dist)
                    drawCircle(dotColor, radius = 6.dp.toPx(), center = Offset(dot1X, resCenter.y))

                    val step2Dist = (boilerCenter.x - pumpCenter.x)
                    val dot2X = pumpCenter.x + (((flowOffset + 50f) % 100f) / 100f * step2Dist)
                    drawCircle(dotColor, radius = 6.dp.toPx(), center = Offset(dot2X, pumpCenter.y))

                    val dx = (outletCenter.x - boilerCenter.x)
                    val dy = (outletCenter.y - boilerCenter.y)
                    val pct3 = (flowOffset / 100f)
                    drawCircle(dotColor, radius = 6.dp.toPx(), center = Offset(boilerCenter.x + dx * pct3, boilerCenter.y + dy * pct3))

                    // Draw Nodes
                    drawCircle(Color(0xFF2196F3), radius = 18.dp.toPx(), center = resCenter) // Water Reservoir
                    drawCircle(Color.Gray, radius = 14.dp.toPx(), center = pumpCenter) // Pump
                    drawCircle(Color(0xFFE57373), radius = 22.dp.toPx(), center = boilerCenter) // Heater

                    // Spray Animation at outlets
                    if (animatedProgress > 0f && animatedProgress < 1.0f) {
                        for (i in 0..4) {
                            val sprayY = outletCenter.y + 12.dp.toPx() + ((flowOffset + i * 20f) % 100f) / 100f * 40.dp.toPx()
                            val sprayX = outletCenter.x + (i - 2) * 5.dp.toPx()
                            drawCircle(dotColor.copy(alpha = 0.6f), radius = 3.dp.toPx(), center = Offset(sprayX, sprayY))
                        }
                    }

                    // Labels
                    // We can't draw text directly easily in Compose Canvas without native canvas, so we keep visual representations
                }

                // Node labels as Compose overlay
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("RESERVOIR", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.TopStart).padding(start = 28.dp, top = 30.dp))
                    Text("PUMP", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.TopCenter).padding(end = 65.dp, top = 30.dp))
                    Text("BOILER", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.TopEnd).padding(end = 100.dp, top = 20.dp))
                    Text("OUTLET", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.BottomEnd).padding(end = 36.dp, bottom = 48.dp))
                }
            }

            Text(
                text = phaseText,
                color = Color.White,
                fontSize = (overlayH.value * 0.030f).coerceIn(10f, 15f).sp,
                fontWeight = FontWeight.Medium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(0.72f)
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
                    fontSize = (overlayH.value * 0.030f).coerceIn(10f, 15f).sp
                )
            }

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRose),
                shape = RoundedCornerShape(4.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text("HALT CYCLE", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}


@Composable
private fun CleaningSliderRow(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    unit: String = "",
    labelSize: androidx.compose.ui.unit.TextUnit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.SemiBold,
                fontSize = labelSize * 0.9f
            )
            Text(
                text = String.format("%.1f%s", value, unit),
                color = Gold,
                fontWeight = FontWeight.Bold,
                fontSize = labelSize
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "-",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 20.sp,
                modifier = Modifier
                    .width(24.dp)
                    .clickable {
                        onValueChange((value - 0.1f).coerceIn(range.start, range.endInclusive))
                    },
                textAlign = TextAlign.Center
            )
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = range,
                colors = SliderDefaults.colors(
                    thumbColor = Gold,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                ),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "+",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 20.sp,
                modifier = Modifier
                    .width(24.dp)
                    .clickable {
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
    labelSize: androidx.compose.ui.unit.TextUnit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.SemiBold,
                fontSize = labelSize * 0.9f
            )
            Text(
                text = "$value$unit",
                color = Gold,
                fontWeight = FontWeight.Bold,
                fontSize = labelSize
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "-",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 20.sp,
                modifier = Modifier
                    .width(24.dp)
                    .clickable {
                        onValueChange((value - step).coerceIn(range.first, range.last))
                    },
                textAlign = TextAlign.Center
            )
            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = range.first.toFloat()..range.last.toFloat(),
                steps = if (step == 1) (range.last - range.first - 1) else ((range.last - range.first) / step - 1),
                colors = SliderDefaults.colors(
                    thumbColor = Gold,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                ),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "+",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 20.sp,
                modifier = Modifier
                    .width(24.dp)
                    .clickable {
                        onValueChange((value + step).coerceIn(range.first, range.last))
                    },
                textAlign = TextAlign.Center
            )
        }
    }
}
