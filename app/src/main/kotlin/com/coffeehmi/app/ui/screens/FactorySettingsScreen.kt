package com.coffeehmi.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
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
import com.coffeehmi.app.ui.theme.SuccessGreen
import kotlinx.coroutines.delay

@Composable
fun FactorySettingsScreen(
    onBack: () -> Unit
) {
    val activePin by InventoryManager.maintenancePin.collectAsState()
    val systemLogs = remember { InventoryManager.systemLogs }

    var newPinInput by remember { mutableStateOf("") }
    var pinMessage by remember { mutableStateOf("ENTER 4-DIGIT NEW PIN") }
    var pinMessageColor by remember { mutableStateOf(Color.White.copy(alpha = 0.6f)) }

    var toastShow by remember { mutableStateOf(false) }
    var toastText by remember { mutableStateOf("") }

    LaunchedEffect(toastShow) {
        if (toastShow) {
            delay(2000)
            toastShow = false
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
                    imageVector = Icons.Default.Key,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(titleSize.value.dp * 1.1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "FACTORY SETTINGS & ACCESS CONTROL",
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

            // Content divided into PIN Pad and System Logs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(outerPad),
                horizontalArrangement = Arrangement.spacedBy(colGap)
            ) {
                // Left Column: Change Maintenance PIN Pad
                Surface(
                    modifier = Modifier
                        .width(cardW)
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
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "CHANGE MAINTENANCE PIN",
                                color = Gold,
                                fontWeight = FontWeight.Bold,
                                fontSize = labelSize,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Used by technicians to access configurations.",
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = labelSize * 0.75f,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        // Display screen for entering PIN
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Masked dots display
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.padding(vertical = 12.dp)
                            ) {
                                for (i in 0 until 4) {
                                    val hasChar = i < newPinInput.length
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(6.dp))
                                            .background(if (hasChar) Gold else Color.Transparent)
                                    )
                                }
                            }

                            Text(
                                text = pinMessage,
                                color = pinMessageColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        // Numeric Keypad Grid
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.width(220.dp)
                        ) {
                            val keypadKeys = listOf(
                                listOf("1", "2", "3"),
                                listOf("4", "5", "6"),
                                listOf("7", "8", "9"),
                                listOf("C", "0", "OK")
                            )

                            keypadKeys.forEach { rowKeys ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    rowKeys.forEach { key ->
                                        val isOk = key == "OK"
                                        val isClear = key == "C"
                                        val keyBg = when {
                                            isOk -> Color(0xFFD32F2F)
                                            isClear -> Color(0x22FFFFFF)
                                            else -> Color(0x0DFFFFFF)
                                        }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(40.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(keyBg)
                                                .clickable {
                                                    when (key) {
                                                        "C" -> {
                                                            if (newPinInput.isNotEmpty()) {
                                                                newPinInput = newPinInput.dropLast(1)
                                                            }
                                                            pinMessage = "ENTER 4-DIGIT NEW PIN"
                                                            pinMessageColor = Color.White.copy(alpha = 0.6f)
                                                        }
                                                        "OK" -> {
                                                            if (newPinInput.length == 4) {
                                                                InventoryManager.setMaintenancePin(newPinInput)
                                                                toastText = "Maintenance PIN changed to $newPinInput"
                                                                toastShow = true
                                                                newPinInput = ""
                                                                pinMessage = "PIN SAVED SUCCESSFULLY!"
                                                                pinMessageColor = SuccessGreen
                                                            } else {
                                                                pinMessage = "ERROR: MUST BE 4 DIGITS"
                                                                pinMessageColor = ErrorRose
                                                            }
                                                        }
                                                        else -> {
                                                            if (newPinInput.length < 4) {
                                                                newPinInput += key
                                                            }
                                                        }
                                                    }
                                                }
                                                .border(1.dp, Color(0x15FFFFFF), RoundedCornerShape(6.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (key == "C" && newPinInput.isEmpty()) {
                                                Icon(
                                                    imageVector = Icons.Default.Backspace,
                                                    contentDescription = "backspace",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            } else {
                                                Text(
                                                    text = key,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Right Column: Diagnostic Logs Viewer
                Surface(
                    modifier = Modifier
                        .width(cardW)
                        .fillMaxHeight()
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(12.dp)),
                    color = Color(0x0AFFFFFF),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "SYSTEM DIAGNOSTIC LOGS",
                                    color = Gold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = labelSize,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Verbose bus telemetry feeds & error codes.",
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontSize = labelSize * 0.75f
                                )
                            }

                            // Simulation actions
                            Button(
                                onClick = {
                                    val simulatedErrors = listOf(
                                        "WARNING: Grinder burrs temperature reached 48.5°C.",
                                        "INFO: Drip tray fluid level sensor verified (Level 12%).",
                                        "DIAG: Steam wand heating cycles verified (PWM 88%).",
                                        "INFO: Group head cleaning solenoid cycle triggered.",
                                        "CRITICAL: Ground waste cake bucket threshold breached (15 cakes)."
                                    )
                                    InventoryManager.addLog(simulatedErrors.random())
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x1EFFFFFF)),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                border = BorderStroke(1.dp, Color(0x22FFFFFF))
                            ) {
                                Text("JOG LOG", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Scrollable list of logs
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F0908))
                                .border(1.dp, Color(0x1EFFFFFF), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(systemLogs) { log ->
                                val isWarning = log.contains("WARNING") || log.contains("ALERT")
                                val isCritical = log.contains("CRITICAL") || log.contains("Error") || log.contains("blocked")
                                val logColor = when {
                                    isCritical -> ErrorRose
                                    isWarning -> Color(0xFFFFB74D)
                                    else -> Color.White.copy(alpha = 0.8f)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = log,
                                        color = logColor,
                                        fontSize = 11.sp,
                                        fontWeight = if (isCritical || isWarning) FontWeight.Bold else FontWeight.Normal,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // System Stats footer inside logs pane
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("CAN BUS FEED: ONLINE", color = SuccessGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("MEM ALLOC: 42.1 MB / 256.0 MB", color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp)
                        }
                    }
                }
            }

            // Bottom Actions: BACK
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = outerPad * 0.5f, end = outerPad, start = outerPad),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x1EFFFFFF)),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                    contentPadding = PaddingValues(horizontal = 28.dp, vertical = 10.dp)
                ) {
                    Text("BACK", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // Saved Pin toast overlay
        if (toastShow) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
                    .background(Color(0xE62E7D32), RoundedCornerShape(6.dp))
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text(
                    text = toastText,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
