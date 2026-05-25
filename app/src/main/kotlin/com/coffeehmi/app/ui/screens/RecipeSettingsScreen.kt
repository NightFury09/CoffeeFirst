package com.coffeehmi.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.coffeehmi.app.model.beverageCatalog
import com.coffeehmi.app.model.InventoryManager

@Composable
fun RecipeSettingsScreen(
    onBack: () -> Unit,
    onTestBrew: (String) -> Unit
) {
    // List of beverages excluding "steam" (which has no recipe settings in Pic 4)
    val recipeDrinks = remember { beverageCatalog.filter { it.id != "steam" } }
    var selectedDrinkId by remember { mutableStateOf(recipeDrinks.firstOrNull()?.id ?: "cap") }

    // Selected recipe parameters
    val currentRecipe = remember(selectedDrinkId) { InventoryManager.getRecipe(selectedDrinkId) }
    
    var beansVal by remember(selectedDrinkId) { mutableStateOf(currentRecipe.coffeeBeans) }
    var waterVal by remember(selectedDrinkId) { mutableStateOf(currentRecipe.brewWater) }
    var milkVal by remember(selectedDrinkId) { mutableStateOf(currentRecipe.hotMilk) }
    var priorityPre by remember(selectedDrinkId) { mutableStateOf(currentRecipe.milkPriorityPre) }

    var saveToastShow by remember { mutableStateOf(false) }

    LaunchedEffect(saveToastShow) {
        if (saveToastShow) {
            kotlinx.coroutines.delay(2000)
            saveToastShow = false
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0908)) // Dark theme background
    ) {
        val w = maxWidth
        val h = maxHeight

        val topBarH = (h.value * 0.12f).coerceIn(48f, 72f).dp
        val contentH = h - topBarH

        val outerPad = (w.value * 0.02f).coerceIn(8f, 20f).dp
        val listW = (w.value * 0.28f).coerceIn(120f, 260f).dp
        val slidersW = w - listW - outerPad * 3

        val titleSize = (topBarH.value * 0.35f).coerceIn(16f, 24f).sp
        val labelSize = (h.value * 0.045f).coerceIn(11f, 18f).sp

        Column(modifier = Modifier.fillMaxSize()) {
            // Header (Pic 4)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topBarH)
                    .padding(horizontal = outerPad),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECIPE SETTINGS",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = titleSize,
                    letterSpacing = 1.sp,
                    modifier = Modifier.weight(1f)
                )

                // Top right status indicators (pic 4)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(topBarH * 0.65f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x1AFFFFFF))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Tune, null, tint = com.coffeehmi.app.ui.theme.Gold, modifier = Modifier.size(topBarH * 0.32f))
                    }
                    Box(
                        modifier = Modifier
                            .size(topBarH * 0.65f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x1AFFFFFF))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Thermostat, null, tint = com.coffeehmi.app.ui.theme.Gold, modifier = Modifier.size(topBarH * 0.32f))
                    }
                }
            }

            // Horizontal Line
            Divider(color = Color.White.copy(alpha = 0.15f), thickness = 1.dp)

            // Content Panel (Split View)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(contentH)
                    .padding(outerPad),
                horizontalArrangement = Arrangement.spacedBy(outerPad)
            ) {
                // Left Side: Vertical Beverage Select List
                Column(
                    modifier = Modifier
                        .width(listW)
                        .fillMaxHeight()
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x05FFFFFF)),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    recipeDrinks.forEach { drink ->
                        val isSelected = drink.id == selectedDrinkId
                        val itemBg = if (isSelected) Color(0xFFD32F2F) else Color.Transparent
                        val textCol = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f) // Use weight to fit all items in the column
                                .background(itemBg)
                                .clickable { selectedDrinkId = drink.id }
                                .border(0.5.dp, Color(0x12FFFFFF))
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = drink.name.uppercase(),
                                color = textCol,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = (labelSize.value * 0.8f).sp,
                                letterSpacing = 0.5.sp,
                                lineHeight = (labelSize.value * 0.9f).sp
                            )
                        }
                    }
                }

                // Right Side: Recipe Sliders & Bottom Controls
                Column(
                    modifier = Modifier
                        .weight(1f) // Use weight for sliders container
                        .fillMaxHeight()
                        .padding(horizontal = 4.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.SpaceEvenly // Distribute space evenly
                    ) {
                        // Slider 1: Coffee Beans
                        RecipeSliderRow(
                            label = "COFFEE BEANS",
                            value = beansVal,
                            onValueChange = { beansVal = it },
                            range = 0.0f..15.0f,
                            labelSize = labelSize
                        )

                        // Slider 2: Brew Water
                        RecipeSliderRow(
                            label = "BREW WATER",
                            value = waterVal,
                            onValueChange = { waterVal = it },
                            range = 0.0f..20.0f,
                            labelSize = labelSize
                        )

                        // Slider 3: Hot Milk
                        RecipeSliderRow(
                            label = "HOT MILK",
                            value = milkVal,
                            onValueChange = { milkVal = it },
                            range = 0.0f..15.0f,
                            labelSize = labelSize
                        )

                        // Milk Priority Buttons (PRE/POST)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MILK PRIORITY",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = labelSize * 0.8f,
                                modifier = Modifier.width(110.dp)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { priorityPre = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (priorityPre) Color(0xFFD32F2F) else Color(0x1EFFFFFF)
                                    ),
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("PRE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { priorityPre = false },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (!priorityPre) Color(0xFFD32F2F) else Color(0x1EFFFFFF)
                                    ),
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("POST", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Bottom Row: BACK, TEST, CANCEL, SAVE
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // BACK Button (bottom left of right panel)
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x1EFFFFFF)),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                            contentPadding = PaddingValues(horizontal = 22.dp, vertical = 8.dp)
                        ) {
                            Text("BACK", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        // SAVE, CANCEL, TEST group
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            // TEST Button (brews a custom test cup)
                            Button(
                                onClick = {
                                    // Save changes temporarily, then brew
                                    InventoryManager.saveRecipe(selectedDrinkId, beansVal, waterVal, milkVal, priorityPre)
                                    onTestBrew(selectedDrinkId)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x15FFFFFF)),
                                border = BorderStroke(1.dp, com.coffeehmi.app.ui.theme.Gold.copy(alpha = 0.7f)),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 8.dp)
                            ) {
                                Text("TEST", color = com.coffeehmi.app.ui.theme.Gold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            // CANCEL Button (revert values)
                            Button(
                                onClick = {
                                    beansVal = currentRecipe.coffeeBeans
                                    waterVal = currentRecipe.brewWater
                                    milkVal = currentRecipe.hotMilk
                                    priorityPre = currentRecipe.milkPriorityPre
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x15FFFFFF)),
                                border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 8.dp)
                            ) {
                                Text("CANCEL", color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            // SAVE Button
                            Button(
                                onClick = {
                                    InventoryManager.saveRecipe(selectedDrinkId, beansVal, waterVal, milkVal, priorityPre)
                                    saveToastShow = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                            ) {
                                Text("SAVE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
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
                    text = "Recipe settings saved successfully!",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun RecipeSliderRow(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    labelSize: androidx.compose.ui.unit.TextUnit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = labelSize * 0.85f,
            modifier = Modifier.width(130.dp)
        )
        Text(
            text = "-",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 20.sp,
            modifier = Modifier.width(16.dp).clickable {
                onValueChange((value - 0.1f).coerceIn(range.start, range.endInclusive))
            },
            textAlign = TextAlign.Center
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = com.coffeehmi.app.ui.theme.Gold,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
            ),
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
        )
        Text(
            text = "+",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 20.sp,
            modifier = Modifier.width(16.dp).clickable {
                onValueChange((value + 0.1f).coerceIn(range.start, range.endInclusive))
            },
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.width(16.dp))
        // Rounded box showing value in red/orange text
        Surface(
            modifier = Modifier
                .width(60.dp)
                .height(30.dp)
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(4.dp)),
            color = Color(0xFF1B110F)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = String.format("%.1f", value),
                    color = Color(0xFFFF7043),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
