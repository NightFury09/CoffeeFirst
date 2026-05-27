package com.coffeehmi.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffeehmi.app.model.InventoryManager
import com.coffeehmi.app.model.beverageCatalog
import com.coffeehmi.app.ui.theme.DeepEspresso
import com.coffeehmi.app.ui.theme.Gold
import com.coffeehmi.app.ui.theme.ErrorRose
import com.coffeehmi.app.ui.theme.SuccessGreen
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.delay
import java.time.LocalDateTime

@Composable
fun TechnicalSettingsScreen(
    onRecipeSettingClick: () -> Unit,
    onCleaningSettingsClick: () -> Unit,
    onBrewerMotorSettingsClick: () -> Unit,
    onMotorMovementClick: () -> Unit,
    onBack: () -> Unit
) {
    var activeDialog by remember { mutableStateOf<String?>(null) }
    
    // Component test states
    var boilerTest by remember { mutableStateOf(false) }
    var grinderTest by remember { mutableStateOf(false) }
    var valveTest by remember { mutableStateOf(false) }
    var pumpTest by remember { mutableStateOf(false) }

    // Service mode state
    var serviceModeBypass by remember { mutableStateOf(false) }
    var debugLogToggle by remember { mutableStateOf(false) }

    val groundBinCount by InventoryManager.groundBinCount.collectAsState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepEspresso)
    ) {
        val screenW = maxWidth
        val screenH = maxHeight

        val topBarH = (screenH.value * 0.12f).coerceIn(48f, 72f).dp
        val contentH = screenH - topBarH

        val outerPad = (screenW.value * 0.03f).coerceIn(12f, 24f).dp
        val gap = (screenW.value * 0.02f).coerceIn(8f, 16f).dp

        val columns = 5
        val rows = 2
        val cardW = (screenW - outerPad * 2 - gap * (columns - 1)) / columns
        val backBtnH = (screenH.value * 0.08f).coerceIn(40f, 60f).dp
        // Cards fill remaining height: screenH - topBarH - backBtnH - gaps - outer padding
        val cardH = (screenH - topBarH - backBtnH - outerPad - gap) / rows - gap / 2

        val titleSize = (topBarH.value * 0.35f).coerceIn(16f, 26f).sp
        val labelSize = (cardH.value * 0.12f).coerceIn(9f, 14f).sp

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topBarH)
                    .padding(horizontal = outerPad),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TECHNICAL SETTINGS",
                    fontWeight = FontWeight.Bold,
                    fontSize = titleSize,
                    color = Gold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.weight(1f)
                )
            }

            // Grid Layout: content fills weight(1f), cards stretch to fill
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = outerPad, vertical = gap * 0.5f),
                verticalArrangement = Arrangement.spacedBy(gap)
            ) {
                // Row 1
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(gap)
                ) {
                    // 1. RECIPE SETTING
                    IconButtonCard(
                        title = "RECIPE SETTING",
                        icon = Icons.Default.LocalCafe,
                        iconColor = Gold,
                        cardW = cardW,
                        cardH = cardH,
                        labelSize = labelSize,
                        onClick = onRecipeSettingClick
                    )

                    // 2. Counter Reset
                    IconButtonCard(
                        title = "Counter Reset",
                        icon = Icons.Default.FilterList,
                        iconColor = Color.White,
                        cardW = cardW,
                        cardH = cardH,
                        labelSize = labelSize,
                        onClick = { activeDialog = "COUNTER_RESET" }
                    )

                    // 3. Ground Bin Counter
                    IconButtonCard(
                        title = "Ground Bin Counter",
                        icon = Icons.Default.DeleteSweep,
                        iconColor = if (groundBinCount >= InventoryManager.MAX_GROUND_BIN) ErrorRose else Color.White,
                        cardW = cardW,
                        cardH = cardH,
                        labelSize = labelSize,
                        badge = if (groundBinCount >= InventoryManager.MAX_GROUND_BIN) "FULL" else "$groundBinCount/${InventoryManager.MAX_GROUND_BIN}",
                        onClick = { activeDialog = "GROUND_BIN" }
                    )

                    // 4. Brewer Motor Settings
                    IconButtonCard(
                        title = "Brewer Motor Settings",
                        icon = Icons.Default.SettingsInputComponent,
                        iconColor = Color.White,
                        cardW = cardW,
                        cardH = cardH,
                        labelSize = labelSize,
                        onClick = onBrewerMotorSettingsClick
                    )

                    // 5. Cleaning Settings
                    IconButtonCard(
                        title = "Cleaning Settings",
                        icon = Icons.Default.CleaningServices,
                        iconColor = Color.White,
                        cardW = cardW,
                        cardH = cardH,
                        labelSize = labelSize,
                        onClick = onCleaningSettingsClick
                    )
                }

                // Row 2
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(gap)
                ) {
                    // 6. Date Time Settings
                    IconButtonCard(
                        title = "Date Time Settings",
                        icon = Icons.Default.Schedule,
                        iconColor = Color.White,
                        cardW = cardW,
                        cardH = cardH,
                        labelSize = labelSize,
                        onClick = { activeDialog = "DATE_TIME" }
                    )

                    // 7. Machine Details
                    IconButtonCard(
                        title = "Machine Details",
                        icon = Icons.Default.Info,
                        iconColor = Color.White,
                        cardW = cardW,
                        cardH = cardH,
                        labelSize = labelSize,
                        onClick = { activeDialog = "MACHINE_DETAILS" }
                    )

                    // 8. Component Test
                    IconButtonCard(
                        title = "Component Test",
                        icon = Icons.Default.FactCheck,
                        iconColor = Color.White,
                        cardW = cardW,
                        cardH = cardH,
                        labelSize = labelSize,
                        onClick = { activeDialog = "COMPONENT_TEST" }
                    )

                    // 9. SERVICE MODE
                    IconButtonCard(
                        title = "SERVICE MODE",
                        icon = Icons.Default.Engineering,
                        iconColor = Color.White,
                        cardW = cardW,
                        cardH = cardH,
                        labelSize = labelSize,
                        badge = if (serviceModeBypass) "ACTIVE" else null,
                        onClick = { activeDialog = "SERVICE_MODE" }
                    )

                    // 10. MOTOR MOVEMENT
                    IconButtonCard(
                        title = "MOTOR MOVEMENT",
                        icon = Icons.Default.CompassCalibration,
                        iconColor = Color.White,
                        cardW = cardW,
                        cardH = cardH,
                        labelSize = labelSize,
                        onClick = onMotorMovementClick
                    )
                }
            }

            // Bottom fixed-height back button row - always visible
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(backBtnH)
                    .padding(horizontal = outerPad, vertical = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRose),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "BACK",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = labelSize
                    )
                }
            }
        }

        // ── Dialog Overlays ──────────────────────────────────────────────────
        when (activeDialog) {
            "COUNTER_RESET" -> {
                AlertDialog(
                    onDismissRequest = { activeDialog = null },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                InventoryManager.addLog("Beverage counters reset successfully.")
                                activeDialog = null
                            }
                        ) { Text("RESET ALL", color = ErrorRose, fontWeight = FontWeight.Bold) }
                    },
                    dismissButton = {
                        TextButton(onClick = { activeDialog = null }) { Text("CANCEL", color = Color.White) }
                    },
                    title = { Text("Beverage Counter Stats") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("• Espresso: 42 cups dispensed")
                            Text("• Cappuchino: 18 cups dispensed")
                            Text("• Café Latte: 24 cups dispensed")
                            Text("• Americano: 31 cups dispensed")
                            Text("• DipTea: 9 cups dispensed")
                            Text("• Total Cycles: 124 brews")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Click below to clear all statistics and reset log count.", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                        }
                    }
                )
            }
            "GROUND_BIN" -> {
                AlertDialog(
                    onDismissRequest = { activeDialog = null },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                InventoryManager.emptyGroundBin()
                                activeDialog = null
                            }
                        ) { Text("EMPTY WASTE BIN", color = SuccessGreen, fontWeight = FontWeight.Bold) }
                    },
                    dismissButton = {
                        TextButton(onClick = { activeDialog = null }) { Text("CANCEL", color = Color.White) }
                    },
                    title = { Text("Ground Bin Counter") },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text("Current coffee cakes: $groundBinCount / ${InventoryManager.MAX_GROUND_BIN}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(16.dp))
                            LinearProgressIndicator(
                                progress = groundBinCount.toFloat() / InventoryManager.MAX_GROUND_BIN.toFloat(),
                                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                                color = if (groundBinCount >= InventoryManager.MAX_GROUND_BIN) ErrorRose else Gold,
                                trackColor = Color.White.copy(alpha = 0.2f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (groundBinCount >= InventoryManager.MAX_GROUND_BIN) {
                                    "⚠️ MACHINE BLOCKED! Empty the waste tray to proceed."
                                } else {
                                    "Emptying the drawer will reset this counter to zero."
                                },
                                textAlign = TextAlign.Center,
                                color = if (groundBinCount >= InventoryManager.MAX_GROUND_BIN) ErrorRose else Color.White.copy(alpha = 0.7f),
                                fontSize = 13.sp
                            )
                        }
                    }
                )
            }
            "DATE_TIME" -> {
                val currentVirtual = remember { InventoryManager.getSystemDateTime() }
                var localYear by remember { mutableStateOf(currentVirtual.year) }
                var localMonth by remember { mutableStateOf(currentVirtual.monthValue) }
                var localDay by remember { mutableStateOf(currentVirtual.dayOfMonth) }
                var localHour by remember { mutableStateOf(currentVirtual.hour) }
                var localMin by remember { mutableStateOf(currentVirtual.minute) }

                val maxDays = remember(localYear, localMonth) {
                    java.time.YearMonth.of(localYear, localMonth).lengthOfMonth()
                }
                LaunchedEffect(maxDays) {
                    if (localDay > maxDays) {
                        localDay = maxDays
                    }
                }

                AlertDialog(
                    onDismissRequest = { activeDialog = null },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val targetDateTime = LocalDateTime.of(localYear, localMonth, localDay, localHour, localMin)
                                InventoryManager.setSystemDateTime(targetDateTime)
                                activeDialog = null
                            }
                        ) { Text("SYNC DATE & TIME", color = Gold, fontWeight = FontWeight.Bold) }
                    },
                    dismissButton = {
                        TextButton(onClick = { activeDialog = null }) { Text("CANCEL", color = Color.White) }
                    },
                    title = { Text("Adjust System Date & Time", color = Gold, fontWeight = FontWeight.Bold) },
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Date Column
                            Column(
                                modifier = Modifier.weight(1.4f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("DATE SETTINGS", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    TouchStepper(
                                        label = "Day",
                                        value = String.format("%02d", localDay),
                                        onDecrement = { if (localDay > 1) localDay-- },
                                        onIncrement = { if (localDay < maxDays) localDay++ },
                                        modifier = Modifier.weight(1f)
                                    )
                                    TouchStepper(
                                        label = "Month",
                                        value = java.time.format.TextStyle.SHORT.let { style ->
                                            java.time.Month.of(localMonth).getDisplayName(style, java.util.Locale.US)
                                        },
                                        onDecrement = { if (localMonth > 1) localMonth-- else localMonth = 12 },
                                        onIncrement = { if (localMonth < 12) localMonth++ else localMonth = 1 },
                                        modifier = Modifier.weight(1.1f)
                                    )
                                    TouchStepper(
                                        label = "Year",
                                        value = localYear.toString(),
                                        onDecrement = { if (localYear > 2020) localYear-- },
                                        onIncrement = { if (localYear < 2050) localYear++ },
                                        modifier = Modifier.weight(1.2f)
                                    )
                                }
                            }

                            // Time Column
                            Column(
                                modifier = Modifier.weight(1.0f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("TIME SETTINGS", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    TouchStepper(
                                        label = "Hour",
                                        value = String.format("%02d", localHour),
                                        onDecrement = { if (localHour > 0) localHour-- else localHour = 23 },
                                        onIncrement = { if (localHour < 23) localHour++ else localHour = 0 },
                                        modifier = Modifier.weight(1f)
                                    )
                                    TouchStepper(
                                        label = "Min",
                                        value = String.format("%02d", localMin),
                                        onDecrement = { if (localMin > 0) localMin-- else localMin = 59 },
                                        onIncrement = { if (localMin < 59) localMin++ else localMin = 0 },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                )
            }
            "MACHINE_DETAILS" -> {
                AlertDialog(
                    onDismissRequest = { activeDialog = null },
                    confirmButton = {
                        TextButton(onClick = { activeDialog = null }) { Text("OK", color = Gold) }
                    },
                    title = { Text("System Information") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("• Module: Quectel SC200EE")
                            Text("• Target Display: 1280x800 Landscape")
                            Text("• Android Version: 13 (AOSP-based)")
                            Text("• SDK Level: API 33")
                            Text("• App Version: 2.1.0-RC")
                            Text("• Total Run Uptime: 452 hrs")
                            Text("• Boiler Temp Value: 92.4°C / 118.0°C")
                            Text("• Board Voltage: 5.04 V")
                        }
                    }
                )
            }
            "COMPONENT_TEST" -> {
                AlertDialog(
                    onDismissRequest = { activeDialog = null },
                    confirmButton = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { activeDialog = "AUTO_CALIBRATION" }
                            ) { Text("RUN AUTO-CALIBRATION", color = Gold, fontWeight = FontWeight.Bold) }
                            TextButton(
                                onClick = {
                                    boilerTest = false
                                    grinderTest = false
                                    valveTest = false
                                    pumpTest = false
                                    InventoryManager.addLog("Finished component manual tests.")
                                    activeDialog = null
                                }
                            ) { Text("FINISH TEST", color = Color.White) }
                        }
                    },
                    title = { Text("Component Test Panel") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            Text("Toggle hardware components manually to test electrical signals.", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                            Divider(color = Color.White.copy(alpha = 0.15f))
                            
                            ComponentRow("Boiler Heater Element", boilerTest, { boilerTest = it })
                            ComponentRow("Grinder Motor Burrs", grinderTest, { grinderTest = it })
                            ComponentRow("Water Valve Solenoid", valveTest, { valveTest = it })
                            ComponentRow("Milk Air whip Pump", pumpTest, { pumpTest = it })
                        }
                    }
                )
            }
            "AUTO_CALIBRATION" -> {
                SelfCalibrationOverlay(
                    onDismiss = {
                        activeDialog = "COMPONENT_TEST"
                    }
                )
            }
            "SERVICE_MODE" -> {
                AlertDialog(
                    onDismissRequest = { activeDialog = null },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                InventoryManager.addLog("Service mode config saved. Sensor Bypass: $serviceModeBypass")
                                activeDialog = null
                            }
                        ) { Text("SAVE", color = Gold) }
                    },
                    dismissButton = {
                        TextButton(onClick = { activeDialog = null }) { Text("CANCEL", color = Color.White) }
                    },
                    title = { Text("Technician Service Mode") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            Text("Use overrides to run the HMI without full sensor validation loops.", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                            
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Column {
                                    Text("Sensor Validation Bypass", fontWeight = FontWeight.Bold)
                                    Text("Ignore empty errors for custom demo", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                                }
                                Switch(checked = serviceModeBypass, onCheckedChange = { serviceModeBypass = it })
                            }
                            
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Column {
                                    Text("Verbose Log Outputs", fontWeight = FontWeight.Bold)
                                    Text("Log sub-system activities to monitor state", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                                }
                                Switch(checked = debugLogToggle, onCheckedChange = { debugLogToggle = it })
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ComponentRow(name: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.SemiBold)
            if (checked) {
                Text("Testing: Active (Normal Current)", color = SuccessGreen, fontSize = 11.sp)
            } else {
                Text("Testing: Idle", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun IconButtonCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    cardW: Dp,
    cardH: Dp,
    labelSize: androidx.compose.ui.unit.TextUnit,
    badge: String? = null,
    onClick: () -> Unit
) {
    // Fill computed card width + full row height for consistent layout
    Surface(
        modifier = Modifier
            .width(cardW)
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(12.dp)),
        color = Color(0x0AFFFFFF),
        shape = RoundedCornerShape(12.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val h = maxHeight
            val iconFrameSize = (h.value * 0.38f).coerceIn(28f, 80f).dp
            val iconSize = (h.value * 0.20f).coerceIn(16f, 42f).dp
            val badgeFontSize = (h.value * 0.07f).coerceIn(7f, 10f).sp
            val cardLabelSize = (h.value * 0.11f).coerceIn(8f, 14f).sp

            if (badge != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(
                            if (badge == "FULL") ErrorRose else Color(0x33FFFFFF),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(text = badge, fontSize = badgeFontSize, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon frame
                Box(
                    modifier = Modifier
                        .size(iconFrameSize)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x0FFFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(iconSize)
                    )
                }
                Spacer(modifier = Modifier.height((h.value * 0.06f).coerceIn(4f, 12f).dp))
                Text(
                    text = title,
                    fontSize = cardLabelSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = (cardLabelSize.value * 1.2f).sp
                )
            }
        }
    }
}

@Composable
fun SelfCalibrationOverlay(onDismiss: () -> Unit) {
    var progress by remember { mutableStateOf(0f) }
    var phaseText by remember { mutableStateOf("Initializing Sensors...") }
    var tempVal by remember { mutableStateOf(25.0f) }
    var pressureVal by remember { mutableStateOf(0.0f) }

    LaunchedEffect(Unit) {
        val steps = 60
        for (i in 0..steps) {
            val ratio = i.toFloat() / steps.toFloat()
            progress = ratio * 0.95f
            tempVal = 25.0f + ratio * (92.4f - 25.0f)
            pressureVal = ratio * 9.0f
            
            if (ratio <= 0.25f) {
                phaseText = "Testing Thermocouple & Heating Element..."
            } else if (ratio <= 0.5f) {
                phaseText = "Pressurizing Brew Chamber (Sweep)..."
            } else if (ratio <= 0.75f) {
                phaseText = "Calibrating Flowmeter Pulses..."
            } else {
                phaseText = "Finalizing Calibration Parameters..."
            }
            delay(100)
        }
        progress = 1.0f
        tempVal = 92.4f
        pressureVal = 9.0f
        phaseText = "Calibration Successful! Values verified."
        delay(1500)
        onDismiss()
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 100),
        label = "calibration_progress"
    )

    val animatedTemp by animateFloatAsState(
        targetValue = tempVal,
        animationSpec = tween(durationMillis = 100),
        label = "temp_value"
    )

    val animatedPressure by animateFloatAsState(
        targetValue = pressureVal,
        animationSpec = tween(durationMillis = 100),
        label = "pressure_value"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        val overlayW = maxWidth
        val overlayH = maxHeight
        val titleSp = (overlayH.value * 0.045f).coerceIn(14f, 22f).sp
        val panelH = (overlayH.value * 0.34f).coerceIn(140f, 260f).dp
        val dialSize = (overlayH.value * 0.24f).coerceIn(100f, 175f).dp
        val bodyPad = (overlayW.value * 0.04f).coerceIn(12f, 36f).dp

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = bodyPad, vertical = (overlayH.value * 0.03f).coerceIn(8f, 24f).dp)
        ) {
            Text(
                text = "AUTOMATIC SENSOR CALIBRATION",
                color = Gold,
                fontWeight = FontWeight.Bold,
                fontSize = titleSp,
                letterSpacing = 2.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .height(panelH)
                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(12.dp))
                    .background(Color(0x05FFFFFF))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dial 1: Temperature (25°C to 120°C / 92.4°C target)
                Box(
                    modifier = Modifier.size(dialSize),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val radius = size.width * 0.4f
                        
                        drawArc(
                            color = Color(0x22FFFFFF),
                            startAngle = 135f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                        
                        val maxTemp = 120f
                        val minTemp = 20f
                        val tempRatio = ((animatedTemp - minTemp) / (maxTemp - minTemp)).coerceIn(0f, 1f)
                        val sweepAngle = tempRatio * 270f
                        
                        val tempColor = when {
                            animatedTemp < 50f -> Color(0xFF2196F3)
                            animatedTemp < 80f -> Color(0xFFFFB74D)
                            else -> Color(0xFFE57373)
                        }
                        
                        drawArc(
                            color = tempColor,
                            startAngle = 135f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )

                        val numTicks = 10
                        for (i in 0..numTicks) {
                            val angle = 135f + (i.toFloat() / numTicks) * 270f
                            val angleRad = Math.toRadians(angle.toDouble())
                            val innerPoint = Offset(
                                (center.x + (radius - 12.dp.toPx()) * Math.cos(angleRad)).toFloat(),
                                (center.y + (radius - 12.dp.toPx()) * Math.sin(angleRad)).toFloat()
                            )
                            val outerPoint = Offset(
                                (center.x + radius * Math.cos(angleRad)).toFloat(),
                                (center.y + radius * Math.sin(angleRad)).toFloat()
                            )
                            drawLine(
                                color = Color.White.copy(alpha = 0.4f),
                                start = innerPoint,
                                end = outerPoint,
                                strokeWidth = 2.dp.toPx()
                            )
                        }

                        val needleAngle = 135f + tempRatio * 270f
                        val needleRad = Math.toRadians(needleAngle.toDouble())
                        val needleEnd = Offset(
                            (center.x + (radius - 8.dp.toPx()) * Math.cos(needleRad)).toFloat(),
                            (center.y + (radius - 8.dp.toPx()) * Math.sin(needleRad)).toFloat()
                        )
                        drawLine(
                            color = Gold,
                            start = center,
                            end = needleEnd,
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        
                        drawCircle(color = DeepEspresso, radius = 8.dp.toPx())
                        drawCircle(color = Gold, radius = 4.dp.toPx())
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = (dialSize.value * 0.28f).dp)
                    ) {
                        Text(
                            text = String.format("%.1f°C", animatedTemp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = (overlayH.value * 0.032f).coerceIn(10f, 18f).sp
                        )
                        Text(
                            text = "TEMPERATURE",
                            color = Color.White.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold,
                            fontSize = (overlayH.value * 0.018f).coerceIn(7f, 11f).sp
                        )
                    }
                }

                // Dial 2: Pressure (0.0 Bar to 12.0 Bar / 9.0 Bar target)
                Box(
                    modifier = Modifier.size(dialSize),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val radius = size.width * 0.4f
                        
                        drawArc(
                            color = Color(0x22FFFFFF),
                            startAngle = 135f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                        
                        val maxPressure = 12f
                        val pressRatio = (animatedPressure / maxPressure).coerceIn(0f, 1f)
                        val sweepAngle = pressRatio * 270f
                        
                        val pressColor = when {
                            animatedPressure < 4f -> Color(0xFF2196F3)
                            animatedPressure < 8.0f -> Color(0xFF81C784)
                            animatedPressure < 10.0f -> Color(0xFFFFF176)
                            else -> Color(0xFFE57373)
                        }
                        
                        drawArc(
                            color = pressColor,
                            startAngle = 135f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )

                        val numTicks = 12
                        for (i in 0..numTicks) {
                            val angle = 135f + (i.toFloat() / numTicks) * 270f
                            val angleRad = Math.toRadians(angle.toDouble())
                            val innerPoint = Offset(
                                (center.x + (radius - 12.dp.toPx()) * Math.cos(angleRad)).toFloat(),
                                (center.y + (radius - 12.dp.toPx()) * Math.sin(angleRad)).toFloat()
                            )
                            val outerPoint = Offset(
                                (center.x + radius * Math.cos(angleRad)).toFloat(),
                                (center.y + radius * Math.sin(angleRad)).toFloat()
                            )
                            drawLine(
                                color = Color.White.copy(alpha = 0.4f),
                                start = innerPoint,
                                end = outerPoint,
                                strokeWidth = 2.dp.toPx()
                            )
                        }

                        val needleAngle = 135f + pressRatio * 270f
                        val needleRad = Math.toRadians(needleAngle.toDouble())
                        val needleEnd = Offset(
                            (center.x + (radius - 8.dp.toPx()) * Math.cos(needleRad)).toFloat(),
                            (center.y + (radius - 8.dp.toPx()) * Math.sin(needleRad)).toFloat()
                        )
                        drawLine(
                            color = Gold,
                            start = center,
                            end = needleEnd,
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        
                        drawCircle(color = DeepEspresso, radius = 8.dp.toPx())
                        drawCircle(color = Gold, radius = 4.dp.toPx())
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = (dialSize.value * 0.28f).dp)
                    ) {
                        Text(
                            text = String.format("%.1f Bar", animatedPressure),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = (overlayH.value * 0.032f).coerceIn(10f, 18f).sp
                        )
                        Text(
                            text = "PRESSURE",
                            color = Color.White.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold,
                            fontSize = (overlayH.value * 0.018f).coerceIn(7f, 11f).sp
                        )
                    }
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
                Text("ABORT CALIBRATION", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TouchStepper(
    label: String,
    value: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 9.sp,
            color = Color.White.copy(alpha = 0.5f),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x0FFFFFFF), RoundedCornerShape(8.dp))
                .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(8.dp))
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = "-",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onDecrement() },
                textAlign = TextAlign.Center
            )
            Text(
                text = value,
                color = Gold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1.5f),
                textAlign = TextAlign.Center
            )
            Text(
                text = "+",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onIncrement() },
                textAlign = TextAlign.Center
            )
        }
    }
}
