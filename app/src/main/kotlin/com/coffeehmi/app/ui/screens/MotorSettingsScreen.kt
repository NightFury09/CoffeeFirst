package com.coffeehmi.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffeehmi.app.model.InventoryManager
import com.coffeehmi.app.ui.theme.DeepEspresso
import com.coffeehmi.app.ui.theme.Gold
import com.coffeehmi.app.ui.theme.ErrorRose
import com.coffeehmi.app.ui.theme.SuccessGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.compose.ui.graphics.drawscope.rotate

data class MotorInfo(
    val id: String,
    val name: String,
    val function: String,
    val defaultSpeed: Float = 60f,
    val maxCurrent: Float = 2.5f
)

@Composable
fun MotorSettingsScreen(
    isMovementScreen: Boolean,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val currentModel by InventoryManager.machineModel.collectAsState()

    // Define motors for each machine type
    val compactMotors = remember {
        listOf(
            MotorInfo("grinder", "Grinder Burrs", "Grinds coffee beans", 80f, 3.0f),
            MotorInfo("brewer", "Brewer Piston", "Compresses coffee chamber", 50f, 2.5f),
            MotorInfo("pump", "Water Pump", "Pumps hot water through brewer", 70f, 2.0f)
        )
    }

    val standardMotors = remember {
        listOf(
            MotorInfo("grinder", "Grinder Burrs", "Grinds coffee beans", 80f, 3.0f),
            MotorInfo("brewer", "Brewer Piston", "Compresses coffee chamber", 50f, 2.5f),
            MotorInfo("pump", "Water Pump", "Pumps hot water through brewer", 70f, 2.0f),
            MotorInfo("mixer1", "Milk Whip Mixer", "Whips milk powder for crema", 90f, 1.5f),
            MotorInfo("auger1", "Choco Auger Motor", "Dispenses chocolate powder", 40f, 1.8f)
        )
    }

    val premiumMotors = remember {
        listOf(
            MotorInfo("grinder", "Grinder Burrs", "Grinds coffee beans", 80f, 3.0f),
            MotorInfo("brewer", "Brewer Piston", "Compresses coffee chamber", 50f, 2.5f),
            MotorInfo("pump", "Water Pump", "Pumps hot water through brewer", 70f, 2.0f),
            MotorInfo("mixer1", "Milk Whip Mixer", "Whips milk powder for crema", 90f, 1.5f),
            MotorInfo("auger1", "Choco Auger Motor", "Dispenses chocolate powder", 40f, 1.8f),
            MotorInfo("mixer2", "Chai Whip Mixer", "Whips tea/chai powder", 90f, 1.5f),
            MotorInfo("auger2", "Chai Auger Motor", "Dispenses tea powder", 40f, 1.8f),
            MotorInfo("fan", "Extractor Fan", "Expels moisture and steam", 100f, 1.0f)
        )
    }

    val activeMotorsList = remember(currentModel) {
        when (currentModel) {
            "Compact" -> compactMotors
            "Premium" -> premiumMotors
            else -> standardMotors
        }
    }

    // Set up local states for speed and current limits
    val motorSpeeds = remember(activeMotorsList) {
        mutableStateMapOf<String, Float>().apply {
            activeMotorsList.forEach { put(it.id, it.defaultSpeed) }
        }
    }

    val motorCurrents = remember(activeMotorsList) {
        mutableStateMapOf<String, Float>().apply {
            activeMotorsList.forEach { put(it.id, it.maxCurrent) }
        }
    }

    val motorReversible = remember(activeMotorsList) {
        mutableStateMapOf<String, Boolean>().apply {
            activeMotorsList.forEach { put(it.id, it.id == "brewer") }
        }
    }

    // States for Motor Movement test screen
    var selectedTestMotorId by remember(activeMotorsList) { mutableStateOf(activeMotorsList.firstOrNull()?.id ?: "") }
    val selectedTestMotor = remember(selectedTestMotorId, activeMotorsList) {
        activeMotorsList.find { it.id == selectedTestMotorId } ?: activeMotorsList.firstOrNull()
    }
    var isMotorRunning by remember { mutableStateOf(false) }
    var testDirectionForward by remember { mutableStateOf(true) }
    var simulatedCurrentDraw by remember { mutableStateOf(0f) }
    var simulatedRpm by remember { mutableStateOf(0) }

    // Coroutine to simulate motor active telemetry (fluctuating current and speed)
    LaunchedEffect(isMotorRunning, selectedTestMotorId) {
        if (isMotorRunning) {
            val targetSpeed = motorSpeeds[selectedTestMotorId] ?: 50f
            while (true) {
                simulatedCurrentDraw = (targetSpeed / 100f) * 1.5f + ((10..30).random() / 100f)
                simulatedRpm = ((targetSpeed / 100f) * 2800f + (-40..40).random()).coerceAtLeast(0f).toInt()
                delay(150)
            }
        } else {
            simulatedCurrentDraw = 0f; simulatedRpm = 0
        }
    }

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
                    imageVector = if (isMovementScreen) Icons.Default.CompassCalibration else Icons.Default.SettingsInputComponent,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(titleSize.value.dp * 1.1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isMovementScreen) "MOTOR MOVEMENT DIAGNOSTICS" else "BREWER MOTOR SETTINGS",
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

            // Content Area
            if (!isMovementScreen) {
                // BREWER MOTOR SETTINGS (Configuration)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(outerPad),
                    horizontalArrangement = Arrangement.spacedBy(colGap)
                ) {
                    // Left Pane: Machine Model Config Selector & Summary
                    Surface(
                        modifier = Modifier
                            .width(w * 0.3f)
                            .fillMaxHeight()
                            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(12.dp)),
                        color = Color(0x0AFFFFFF),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "MACHINE MODEL",
                                color = Gold,
                                fontWeight = FontWeight.Bold,
                                fontSize = labelSize,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Select the machine type to load pre-configured motor clusters.",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = labelSize * 0.8f
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            listOf("Compact", "Standard", "Premium").forEach { model ->
                                val isSelected = (model == currentModel)
                                Button(
                                    onClick = {
                                        InventoryManager.machineModel.value = model
                                        InventoryManager.addLog("Machine model configured to: $model")
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) Color(0xFFD32F2F) else Color(0x15FFFFFF)
                                    ),
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(1.dp, if (isSelected) Color.Transparent else Color(0x22FFFFFF))
                                ) {
                                    val count = when (model) {
                                        "Compact" -> 3
                                        "Premium" -> 8
                                        else -> 5
                                    }
                                    Text("$model ($count Motors)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0x12FFFFFF), RoundedCornerShape(8.dp)),
                                color = Color(0x05FFFFFF)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Active Model: $currentModel", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("Active Motors: ${activeMotorsList.size}", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                    Text("Control Bus: CAN-Bus 2.0", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    // Right Pane: Motor List with sliders
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(12.dp)),
                        color = Color(0x0AFFFFFF),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Text(
                                    text = "MOTOR CALIBRATION PARAMETERS",
                                    color = Gold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = labelSize,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Divider(color = Color.White.copy(alpha = 0.1f))
                            }

                            items(activeMotorsList) { motor ->
                                var speed by remember(motor.id) { mutableStateOf(motorSpeeds[motor.id] ?: motor.defaultSpeed) }
                                var current by remember(motor.id) { mutableStateOf(motorCurrents[motor.id] ?: motor.maxCurrent) }
                                var rev by remember(motor.id) { mutableStateOf(motorReversible[motor.id] ?: false) }

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, Color(0x0DFFFFFF), RoundedCornerShape(8.dp)),
                                    color = Color(0x05FFFFFF)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(motor.name.uppercase(), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                                Text(motor.function, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                                            }

                                            // Reversible Toggle
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("Reversible", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, modifier = Modifier.padding(end = 4.dp))
                                                Checkbox(
                                                    checked = rev,
                                                    onCheckedChange = {
                                                        rev = it
                                                        motorReversible[motor.id] = it
                                                    },
                                                    colors = CheckboxDefaults.colors(
                                                        checkedColor = Gold,
                                                        checkmarkColor = DeepEspresso
                                                    )
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Speed slider
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("PWM Speed", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, modifier = Modifier.width(90.dp))
                                            Slider(
                                                value = speed,
                                                onValueChange = {
                                                    speed = it
                                                    motorSpeeds[motor.id] = it
                                                },
                                                valueRange = 10f..100f,
                                                modifier = Modifier.weight(1f),
                                                colors = SliderDefaults.colors(thumbColor = Gold, activeTrackColor = Color.White)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("${speed.toInt()}%", color = Gold, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.width(36.dp))
                                        }

                                        // Current Limit slider
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Stall Current", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, modifier = Modifier.width(90.dp))
                                            Slider(
                                                value = current,
                                                onValueChange = {
                                                    current = it
                                                    motorCurrents[motor.id] = it
                                                },
                                                valueRange = 0.5f..4.0f,
                                                modifier = Modifier.weight(1f),
                                                colors = SliderDefaults.colors(thumbColor = Gold, activeTrackColor = Color.White)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(String.format("%.1fA", current), color = Gold, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.width(36.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // MOTOR MOVEMENT DIAGNOSTICS (Interactive Jog Test)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(outerPad),
                    horizontalArrangement = Arrangement.spacedBy(colGap)
                ) {
                    // Left Pane: List of motors (clickable to select)
                    Surface(
                        modifier = Modifier
                            .width(w * 0.35f)
                            .fillMaxHeight()
                            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(12.dp)),
                        color = Color(0x0AFFFFFF),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "SELECT MOTOR TO TEST",
                                color = Gold,
                                fontWeight = FontWeight.Bold,
                                fontSize = labelSize,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(activeMotorsList) { motor ->
                                    val isSelected = (motor.id == selectedTestMotorId)
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                if (isMotorRunning) isMotorRunning = false
                                                selectedTestMotorId = motor.id
                                            }
                                            .border(
                                                1.dp,
                                                if (isSelected) Gold else Color(0x12FFFFFF),
                                                RoundedCornerShape(8.dp)
                                            ),
                                        color = if (isSelected) Color(0x15FFFFFF) else Color.Transparent
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = null,
                                                tint = if (isSelected) Gold else Color.White.copy(alpha = 0.5f),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = motor.name,
                                                    color = Color.White,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    fontSize = 12.sp
                                                )
                                                Text(
                                                    text = "Target Speed: ${(motorSpeeds[motor.id] ?: motor.defaultSpeed).toInt()}% PWM",
                                                    color = Color.White.copy(alpha = 0.5f),
                                                    fontSize = 9.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Right Pane: Active Test Panel & Canvas Rotating Gear Animation
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
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Motor header
                            selectedTestMotor?.let { motor ->
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "ACTIVE TEST CHANNEL: ${motor.name.uppercase()}",
                                        color = Gold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = labelSize,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = motor.function,
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontSize = 11.sp
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth().weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    // Animated Gear Canvas
                                    Box(
                                        modifier = Modifier
                                            .size((h.value * 0.4f).coerceIn(120f, 220f).dp)
                                            .border(1.dp, Color(0x12FFFFFF), RoundedCornerShape(100.dp))
                                            .background(Color(0x05FFFFFF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val activeSpeed = motorSpeeds[motor.id] ?: 60f
                                        SpinningGearCanvas(
                                            isSpinning = isMotorRunning,
                                            speedPercent = activeSpeed,
                                            clockwise = testDirectionForward,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    // Telemetry Panel
                                    Column(
                                        modifier = Modifier
                                            .width(w * 0.22f)
                                            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(8.dp))
                                            .background(Color(0x10FFFFFF))
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text("LIVE TELEMETRY", color = Gold, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 0.5.sp)
                                        Divider(color = Color.White.copy(alpha = 0.1f))

                                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                            Text("STATUS:", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                                            Text(
                                                text = if (isMotorRunning) "RUNNING" else "STOPPED",
                                                color = if (isMotorRunning) SuccessGreen else ErrorRose,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        }

                                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                            Text("CURRENT:", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                                            Text(text = String.format("%.2f A", simulatedCurrentDraw), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                        }

                                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                            Text("SPEED:", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                                            Text(text = "$simulatedRpm RPM", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                        }

                                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                            Text("DIRECTION:", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                                            Text(
                                                text = if (testDirectionForward) "FORWARD" else "REVERSE",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }

                                // Interactive Testing Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val isReversible = motorReversible[motor.id] ?: false

                                    if (isReversible) {
                                        Button(
                                            onClick = {
                                                if (isMotorRunning && testDirectionForward) {
                                                    isMotorRunning = false
                                                } else {
                                                    testDirectionForward = true
                                                    isMotorRunning = true
                                                    InventoryManager.addLog("Jogging motor ${motor.name} (FORWARD)")
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isMotorRunning && testDirectionForward) ErrorRose else SuccessGreen
                                            ),
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier.padding(end = 12.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isMotorRunning && testDirectionForward) Icons.Default.Stop else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("TEST FWD", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }

                                        Button(
                                            onClick = {
                                                if (isMotorRunning && !testDirectionForward) {
                                                    isMotorRunning = false
                                                } else {
                                                    testDirectionForward = false
                                                    isMotorRunning = true
                                                    InventoryManager.addLog("Jogging motor ${motor.name} (REVERSE)")
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isMotorRunning && !testDirectionForward) ErrorRose else SuccessGreen
                                            ),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isMotorRunning && !testDirectionForward) Icons.Default.Stop else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("TEST REV", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    } else {
                                        // Standard unidirectional jog button
                                        Button(
                                            onClick = {
                                                isMotorRunning = !isMotorRunning
                                                testDirectionForward = true
                                                if (isMotorRunning) {
                                                    InventoryManager.addLog("Running continuous test for ${motor.name}")
                                                } else {
                                                    InventoryManager.addLog("Stopped test for ${motor.name}")
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isMotorRunning) ErrorRose else SuccessGreen
                                            ),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isMotorRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (isMotorRunning) "STOP MOTOR" else "RUN TEST JOG", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom row: BACK (left) & SAVE (right) for Settings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((h.value * 0.09f).coerceIn(44f, 68f).dp)
                    .padding(horizontal = outerPad, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // BACK button
                Button(
                    onClick = {
                        isMotorRunning = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x1EFFFFFF)),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 6.dp)
                ) {
                    Text("BACK", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                // Show SAVE button only in brewer config settings
                if (!isMovementScreen) {
                    Button(
                        onClick = {
                            // Apply locally set parameters to InventoryManager speed tracker
                            activeMotorsList.forEach { m ->
                                val speed = motorSpeeds[m.id] ?: m.defaultSpeed
                                val mapFlow = InventoryManager.motorSpeeds.getOrPut(m.id) { MutableStateFlow(speed) }
                                mapFlow.value = speed
                            }
                            InventoryManager.addLog("Calibrated motor parameters saved.")
                            saveToastShow = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 6.dp)
                    ) {
                        Text("SAVE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
                    text = "Motor parameters saved successfully!",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun SpinningGearCanvas(
    isSpinning: Boolean,
    speedPercent: Float,
    clockwise: Boolean,
    modifier: Modifier = Modifier
) {
    // Rotating transition when spinning
    val infiniteTransition = rememberInfiniteTransition(label = "gearRotation")
    
    val rotation by if (isSpinning) {
        val baseDuration = 3000
        // Speed percent 10..100 maps to duration 3000ms..300ms
        val duration = (baseDuration - (speedPercent / 100f) * 2700f).toInt().coerceIn(200, 3000)
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = if (clockwise) 360f else -360f,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "spin"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    Canvas(modifier = modifier.padding(16.dp)) {
        val width = size.width
        val height = size.height
        val center = Offset(width / 2, height / 2)
        val outerRadius = size.minDimension * 0.42f
        val innerRadius = outerRadius * 0.65f
        val centerHoleRadius = outerRadius * 0.20f

        val teethCount = 12
        val degToRad = Math.PI / 180.0

        // Rotate drawscope dynamically
        rotate(rotation, center) {
            // Draw teeth path
            val gearPath = Path()
            for (i in 0 until teethCount) {
                val baseAngle = (360f / teethCount) * i
                
                // Four points per tooth to construct realistic gear profiles
                val a1 = baseAngle - 6
                val a2 = baseAngle - 3
                val a3 = baseAngle + 3
                val a4 = baseAngle + 6
                
                // Point coordinates
                val p1x = center.x + innerRadius * cos(a1 * degToRad).toFloat()
                val p1y = center.y + innerRadius * sin(a1 * degToRad).toFloat()

                val p2x = center.x + outerRadius * cos(a2 * degToRad).toFloat()
                val p2y = center.y + outerRadius * sin(a2 * degToRad).toFloat()

                val p3x = center.x + outerRadius * cos(a3 * degToRad).toFloat()
                val p3y = center.y + outerRadius * sin(a3 * degToRad).toFloat()

                val p4x = center.x + innerRadius * cos(a4 * degToRad).toFloat()
                val p4y = center.y + innerRadius * sin(a4 * degToRad).toFloat()

                if (i == 0) {
                    gearPath.moveTo(p1x, p1y)
                } else {
                    gearPath.lineTo(p1x, p1y)
                }
                gearPath.lineTo(p2x, p2y)
                gearPath.lineTo(p3x, p3y)
                gearPath.lineTo(p4x, p4y)
            }
            gearPath.close()

            // Draw gear body
            drawPath(
                path = gearPath,
                color = if (isSpinning) Gold else Color.White.copy(alpha = 0.25f)
            )

            // Inner circle cut-out
            drawCircle(
                color = DeepEspresso,
                radius = innerRadius * 0.85f,
                center = center
            )

            // Outer ring accent
            drawCircle(
                color = if (isSpinning) Gold.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                radius = innerRadius * 0.85f,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )

            // Center shaft and keyway
            drawCircle(
                color = if (isSpinning) Gold else Color.White.copy(alpha = 0.4f),
                radius = centerHoleRadius,
                center = center
            )

            // Shaft keyway block
            drawRect(
                color = DeepEspresso,
                topLeft = Offset(center.x - centerHoleRadius * 0.4f, center.y - centerHoleRadius * 1.3f),
                size = androidx.compose.ui.geometry.Size(centerHoleRadius * 0.8f, centerHoleRadius * 0.9f)
            )
        }
    }
}
