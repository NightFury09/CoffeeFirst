package com.coffeehmi.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffeehmi.app.model.beverageCatalog
import com.coffeehmi.app.model.InventoryManager
import com.coffeehmi.app.ui.theme.TextPrimary

@Composable
fun CustomizationScreen(
    beverageId: String?,
    onConfirm: (String) -> Unit,
    onBack: () -> Unit
) {
    val beverage = beverageCatalog.find { it.id == beverageId }

    // Dynamic Options configuration based on beverageId
    val card1Title = when (beverageId) {
        "tea" -> "Tea Strength"
        "milk" -> "Foam Level"
        "hot" -> "Water Volume"
        "steam" -> "Steam Pressure"
        else -> "Coffee Strength"
    }

    val card2Title = when (beverageId) {
        "steam" -> "Steam Duration"
        else -> "Temperature"
    }

    val showCard3 = when (beverageId) {
        "hot", "steam" -> false
        else -> true
    }
    
    val card3Title = when (beverageId) {
        "esp", "ame", "tea" -> "Water Volume"
        else -> "Milk Selection"
    }

    // Card 1 State: Slider value
    var value1 by remember(beverageId) {
        mutableStateOf(
            when (beverageId) {
                "hot" -> 300f // Default 300ml for hot water
                else -> 2f // Default middle level for strength/foam/pressure
            }
        )
    }

    // Card 2 State: Segmented buttons index
    var value2 by remember(beverageId) {
        mutableStateOf(1) // Default "Hot" or "30s"
    }

    // Card 3 State: Segmented buttons index (if visible)
    var value3 by remember(beverageId) {
        mutableStateOf(
            when (beverageId) {
                "milk" -> 1 // Default "Whole" milk (index 1) for Hot Milk
                else -> 0 // Default "None" or "Small"
            }
        )
    }

    // Full-screen BoxWithConstraints — no Scaffold, no invisible padding leaks
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val screenW = maxWidth
        val screenH = maxHeight

        // Top bar consumes a fixed fraction of screen height
        val topBarH: Dp = (screenH.value * 0.10f).coerceIn(40f, 64f).dp
        // Everything else goes to the content area
        val contentH: Dp = screenH - topBarH

        // Derived sizes based on the true content area
        val outerPad = (screenW.value * 0.018f).coerceIn(4f, 20f).dp
        val colGap = (screenW.value * 0.020f).coerceIn(4f, 24f).dp
        val groupGap = (contentH.value * 0.020f).coerceIn(4f, 18f).dp
        val segH: Dp = (contentH.value * 0.10f).coerceIn(24f, 52f).dp
        val brewBtnH: Dp = (contentH.value * 0.13f).coerceIn(36f, 68f).dp
        val groupPad = (screenW.value * 0.010f).coerceIn(4f, 14f).dp
        val labelSize = (contentH.value * 0.035f).coerceIn(8f, 17f).sp
        val titleSize = (contentH.value * 0.045f).coerceIn(10f, 21f).sp
        val brewSize = (contentH.value * 0.050f).coerceIn(11f, 26f).sp
        val topLabelSz = (topBarH.value * 0.35f).coerceIn(12f, 24f).sp

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top Bar ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topBarH)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = outerPad),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = beverage?.name ?: "Customize",
                    fontWeight = FontWeight.Bold,
                    fontSize = topLabelSz,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // ── Content: exactly contentH tall, no overflow possible ──────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(contentH)
                    .padding(outerPad),
                horizontalArrangement = Arrangement.spacedBy(colGap)
            ) {
                // Left: Beverage image
                Card(
                    modifier = Modifier
                        .weight(0.38f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        beverage?.let {
                            Image(
                                painter = painterResource(id = it.imageRes),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                                    )
                                )
                        )
                    }
                }

                // Right: Controls — distribute space using weights to ensure no overflow
                Column(
                    modifier = Modifier
                        .weight(0.62f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(groupGap)
                ) {
                    // ── Card 1: Slider (Strength, Foam, Volume, Pressure) ──
                    Box(modifier = Modifier.weight(1f)) {
                        CustomizationGroup(card1Title, groupPad, titleSize) {
                            Column {
                                val sliderRange = if (beverageId == "hot") 150f..450f else 1f..3f
                                val sliderSteps = if (beverageId == "hot") 5 else 1
                                Slider(
                                    value = value1,
                                    onValueChange = { value1 = it },
                                    valueRange = sliderRange,
                                    steps = sliderSteps,
                                    modifier = Modifier.height(32.dp),
                                    colors = SliderDefaults.colors(
                                        thumbColor = MaterialTheme.colorScheme.primary,
                                        activeTrackColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    when (beverageId) {
                                        "hot" -> {
                                            Text("150 ml", fontSize = labelSize, color = TextPrimary)
                                            Text("${value1.toInt()} ml", fontSize = labelSize, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                            Text("450 ml", fontSize = labelSize, color = TextPrimary)
                                        }
                                        "milk" -> {
                                            Text("Low Foam", fontSize = labelSize, color = TextPrimary)
                                            Text("High Foam", fontSize = labelSize, color = TextPrimary)
                                        }
                                        "steam" -> {
                                            Text("Low", fontSize = labelSize, color = TextPrimary)
                                            Text("High", fontSize = labelSize, color = TextPrimary)
                                        }
                                        else -> {
                                            Text("Mild", fontSize = labelSize, color = TextPrimary)
                                            Text("Strong", fontSize = labelSize, color = TextPrimary)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── Card 2: Temperature or Duration ──
                    Box(modifier = Modifier.weight(1f)) {
                        CustomizationGroup(card2Title, groupPad, titleSize) {
                            val options = if (beverageId == "steam") {
                                listOf("15s", "30s", "45s", "60s")
                            } else {
                                listOf("Warm", "Hot", "Extra Hot")
                            }
                            SegmentedButtonSection(
                                selected = value2,
                                options = options,
                                onSelect = { value2 = it },
                                segH = segH,
                                labelSize = labelSize
                            )
                        }
                    }

                    // ── Card 3: Milk Selection or Water Volume (Optional) ──
                    if (showCard3) {
                        Box(modifier = Modifier.weight(1f)) {
                            CustomizationGroup(card3Title, groupPad, titleSize) {
                                val options = when (beverageId) {
                                    "milk" -> listOf("Oat", "Whole", "Skim")
                                    "esp", "ame", "tea" -> listOf("Small", "Medium", "Large")
                                    else -> listOf("None", "Oat", "Whole", "Skim")
                                }
                                SegmentedButtonSection(
                                    selected = value3,
                                    options = options,
                                    onSelect = { value3 = it },
                                    segH = segH,
                                    labelSize = labelSize
                                )
                            }
                        }
                    }

                    // Start Brewing button
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(brewBtnH),
                        onClick = {
                            beverageId?.let { id ->
                                // Save customized recipe configuration to InventoryManager before brewing
                                val beans = when (id) {
                                    "esp", "ame", "cap", "lat" -> value1 // Coffee strength
                                    "tea" -> value1 // Steep strength
                                    else -> 0f
                                }
                                val water = when (id) {
                                    "hot" -> value1 / 20f // Translate ml into brewWater coefficient (waterReq = brewWater * 20)
                                    "esp" -> when (value3) { 0 -> 2f; 1 -> 3f; else -> 4f }
                                    "ame" -> when (value3) { 0 -> 8f; 1 -> 12f; else -> 15f }
                                    "tea" -> when (value3) { 0 -> 10f; 1 -> 15f; else -> 20f }
                                    "cap" -> 10.5f
                                    "lat" -> 8.0f
                                    else -> 0f
                                }
                                val milk = when (id) {
                                    "milk" -> 12f // Standard milk volume for hot milk
                                    "cap" -> if (value3 == 0) 0f else 7.8f
                                    "lat" -> if (value3 == 0) 0f else 10.0f
                                    else -> 0f
                                }
                                InventoryManager.saveRecipe(
                                    beverageId = id,
                                    coffeeBeans = beans,
                                    brewWater = water,
                                    hotMilk = milk,
                                    milkPriorityPre = true
                                )
                                onConfirm(id)
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Start Brewing", fontWeight = FontWeight.Bold, fontSize = brewSize)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomizationGroup(
    title: String,
    groupPad: Dp,
    titleSize: androidx.compose.ui.unit.TextUnit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x0DFFFFFF), RoundedCornerShape(14.dp))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(14.dp))
            .padding(groupPad),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            title,
            fontSize = titleSize,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(groupPad * 0.3f))
        content()
    }
}

@Composable
fun SegmentedButtonSection(
    selected: Int,
    options: List<String>,
    onSelect: (Int) -> Unit,
    segH: Dp,
    labelSize: androidx.compose.ui.unit.TextUnit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        options.forEachIndexed { index, title ->
            val isSelected = selected == index
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(segH)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onSelect(index) },
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0x1AFFFFFF),
                border = if (isSelected) null else BorderStroke(1.dp, Color(0x33FFFFFF))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        title,
                        fontSize = labelSize,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else TextPrimary
                    )
                }
            }
        }
    }
}
