package com.coffeehmi.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
fun OperationalSettingsScreen(
    onNavigateToInventory: () -> Unit,
    onBack: () -> Unit
) {
    val coffeeBeansGrams by InventoryManager.coffeeBeansGrams.collectAsState()
    val milkMl by InventoryManager.milkMl.collectAsState()
    val waterMl by InventoryManager.waterMl.collectAsState()
    val groundBinCount by InventoryManager.groundBinCount.collectAsState()

    var showRefillToast by remember { mutableStateOf(false) }

    LaunchedEffect(showRefillToast) {
        if (showRefillToast) {
            delay(2000)
            showRefillToast = false
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
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(titleSize.value.dp * 1.1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "OPERATIONAL SETTINGS",
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
                // Left Column: Inventory Reservoirs
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
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = "INGREDIENT RESERVOIR LEVELS",
                                color = Gold,
                                fontWeight = FontWeight.Bold,
                                fontSize = labelSize * 0.9f,
                                letterSpacing = 1.sp
                            )

                            Divider(color = Color.White.copy(alpha = 0.08f))

                            // Coffee beans level
                            ReservoirRow(
                                name = "Coffee Beans",
                                current = coffeeBeansGrams,
                                max = InventoryManager.MAX_BEANS,
                                unit = "g",
                                color = Color(0xFF8B5A2B),
                                labelSize = labelSize
                            )

                            // Fresh milk level
                            ReservoirRow(
                                name = "Fresh Milk",
                                current = milkMl,
                                max = InventoryManager.MAX_MILK,
                                unit = "ml",
                                color = Color(0xFFE3DAC9),
                                labelSize = labelSize
                            )

                            // Water tank level
                            ReservoirRow(
                                name = "Water Reservoir",
                                current = waterMl,
                                max = InventoryManager.MAX_WATER,
                                unit = "ml",
                                color = Color(0xFF2196F3),
                                labelSize = labelSize
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Refill All button
                            Button(
                                onClick = {
                                    InventoryManager.refillAll()
                                    showRefillToast = true
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = DeepEspresso),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("REFILL ALL", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            // Navigate to detailed inventory cups estimator
                            Button(
                                onClick = onNavigateToInventory,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x1AFFFFFF)),
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(1.dp, Color(0x22FFFFFF))
                            ) {
                                Text("ESTIMATES", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                            }
                        }
                    }
                }

                // Right Column: Dispense Statistics & Analytics
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
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "OPERATIONAL PERFORMANCE",
                                color = Gold,
                                fontWeight = FontWeight.Bold,
                                fontSize = labelSize,
                                letterSpacing = 1.sp
                            )

                            Divider(color = Color.White.copy(alpha = 0.08f))

                            StatRow(label = "Total Cup Dispenses:", value = "124 cups", labelSize = labelSize)
                            StatRow(label = "Waste Bin Ground Cakes:", value = "$groundBinCount / ${InventoryManager.MAX_GROUND_BIN}", valueColor = if (groundBinCount >= InventoryManager.MAX_GROUND_BIN) ErrorRose else Color.White, labelSize = labelSize)
                            StatRow(label = "Self-Cleaning Cycles Run:", value = "18 cycles", labelSize = labelSize)
                            StatRow(label = "Boiler Core Temperature:", value = "92.4°C", labelSize = labelSize)
                            StatRow(label = "Telemetry Connection:", value = "Online (LTE-M)", valueColor = SuccessGreen, labelSize = labelSize)
                        }

                        // Informational card
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0x12FFFFFF), RoundedCornerShape(8.dp)),
                            color = Color(0x05FFFFFF)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Active Diagnostics:", color = Gold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text("Boiler heating unit stable, milk line pressure stable. Diagnostic self-test passed at 04:00 AM.", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, lineHeight = 13.sp)
                            }
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

        // Overlay Notification for refilling
        if (showRefillToast) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
                    .background(Color(0xE62E7D32), RoundedCornerShape(6.dp))
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "All reservoirs successfully refilled!",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun ReservoirRow(
    name: String,
    current: Float,
    max: Float,
    unit: String,
    color: Color,
    labelSize: androidx.compose.ui.unit.TextUnit
) {
    val pct = (current / max).coerceIn(0f, 1f)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name, color = Color.White.copy(alpha = 0.9f), fontWeight = FontWeight.SemiBold, fontSize = labelSize * 0.85f)
            Text(
                text = "${current.toInt()}/${max.toInt()}$unit (${(pct * 100).toInt()}%)",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = labelSize * 0.75f
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = pct,
            color = color,
            trackColor = Color(0x11FFFFFF),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    valueColor: Color = Color.White,
    labelSize: androidx.compose.ui.unit.TextUnit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = labelSize * 0.85f)
        Text(text = value, color = valueColor, fontWeight = FontWeight.Bold, fontSize = labelSize * 0.85f)
    }
}
