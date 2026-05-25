package com.coffeehmi.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffeehmi.app.model.Beverage
import com.coffeehmi.app.model.beverageCatalog
import com.coffeehmi.app.model.InventoryManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeverageSelectScreen(
    onBeverageSelected: (String) -> Unit,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val groundBinCount by InventoryManager.groundBinCount.collectAsState()
    val isBinFull = groundBinCount >= InventoryManager.MAX_GROUND_BIN

    // Dialog state for warnings
    var activeWarningDialog by remember { mutableStateOf<String?>(null) }
    var grinderInfoShow by remember { mutableStateOf(false) }
    var tempInfoShow by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val screenW = maxWidth
        val screenH = maxHeight

        // Top bar height as a fraction of screen height
        val topBarH: Dp = (screenH.value * 0.12f).coerceIn(48f, 72f).dp
         
        val columns = 4
        val rows = 2
        val itemCount = beverageCatalog.size

        // Padding & gaps scale with screen width
        val outerPad: Dp = (screenW.value * 0.015f).coerceIn(8f, 20f).dp
        val gap: Dp = (screenW.value * 0.012f).coerceIn(6f, 16f).dp

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
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = "Select Beverage",
                    fontWeight = FontWeight.Bold,
                    fontSize = (topBarH.value * 0.35f).coerceIn(16f, 24f).sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )

                // ── Status Indicators (Beans/Grind and Boiler Temp) ──
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    // Grinder Status
                    Surface(
                        modifier = Modifier
                            .size(topBarH * 0.7f)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                            .clickable { grinderInfoShow = true },
                        color = Color(0x1AFFFFFF)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Grinder settings",
                                tint = com.coffeehmi.app.ui.theme.Gold,
                                modifier = Modifier.size(topBarH * 0.35f)
                            )
                        }
                    }

                    // Boiler Temp Status
                    Surface(
                        modifier = Modifier
                            .size(topBarH * 0.7f)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                            .clickable { tempInfoShow = true },
                        color = Color(0x1AFFFFFF)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Thermostat,
                                contentDescription = "Boiler temperature",
                                tint = com.coffeehmi.app.ui.theme.Gold,
                                modifier = Modifier.size(topBarH * 0.35f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // ── Warning Banner for Ground Bin ──
            if (isBinFull) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFD32F2F))
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "☕ GROUND BIN FULL! Empty the bin under Maintenance Settings to brew coffee.",
                        color = Color.White,
                        fontSize = 11.sp, // Reduced font size to save space
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ── Beverage Grid (4x2 layout) ────────────────────────────────────
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = outerPad, vertical = outerPad / 2)
            ) {
                val gridW = maxWidth
                val gridH = maxHeight
                
                val cardW: Dp = (gridW - gap * (columns - 1)) / columns
                val cardH: Dp = (gridH - gap * (rows - 1)) / rows

                Column(verticalArrangement = Arrangement.spacedBy(gap)) {
                    for (row in 0 until rows) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(cardH),
                            horizontalArrangement = Arrangement.spacedBy(gap)
                        ) {
                            for (col in 0 until columns) {
                                val index = row * columns + col
                                if (index < itemCount) {
                                    val beverage = beverageCatalog[index]
                                    val hasIngredients = InventoryManager.canBrew(beverage.id)
                                    val isCoffee = beverage.id in listOf("cap", "lat", "esp", "ame")
                                    val isBlocked = !hasIngredients || (isCoffee && isBinFull)

                                    BeverageCard(
                                        beverage = beverage,
                                        cardWidth = cardW,
                                        cardHeight = cardH,
                                        isBlocked = isBlocked,
                                        isBinFull = isBinFull && isCoffee,
                                        onClick = {
                                            if (isCoffee && isBinFull) {
                                                activeWarningDialog = "BIN_FULL"
                                            } else if (!hasIngredients) {
                                                activeWarningDialog = "LOW_INGREDIENTS_${beverage.id}"
                                            } else {
                                                onBeverageSelected(beverage.id)
                                            }
                                        }
                                    )
                                } else {
                                    Spacer(modifier = Modifier.width(cardW))
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Dialogs ────────────────────────────────────────────────────────
        if (activeWarningDialog != null) {
            val dialogType = activeWarningDialog!!
            AlertDialog(
                onDismissRequest = { activeWarningDialog = null },
                confirmButton = {
                    TextButton(onClick = { activeWarningDialog = null }) {
                        Text("OK", color = com.coffeehmi.app.ui.theme.Gold)
                    }
                },
                title = {
                    Text(
                        text = if (dialogType == "BIN_FULL") "Machine Blocked" else "Insufficient Ingredients",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = if (dialogType == "BIN_FULL") {
                            "The coffee ground waste bin is full (Limit: 15 cups).\nPlease access Maintenance Mode -> Ground Bin Counter and empty the bin to resume coffee dispensing."
                        } else {
                            val drinkId = dialogType.removePrefix("LOW_INGREDIENTS_")
                            val drink = beverageCatalog.find { it.id == drinkId }
                            val (beans, milk, water) = InventoryManager.getRecipePhysicalRequirements(drinkId)
                            "Cannot prepare ${drink?.name ?: "this drink"}.\n" +
                                    "Required ingredients exceed current levels:\n" +
                                    "• Beans: ${beans.toInt()}g\n" +
                                    "• Milk: ${milk.toInt()}ml\n" +
                                    "• Water: ${water.toInt()}ml\n\n" +
                                    "Refill ingredients in Maintenance Mode."
                        }
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface,
                textContentColor = MaterialTheme.colorScheme.onSurface,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            )
        }

        // Grinder Info Dialog
        if (grinderInfoShow) {
            AlertDialog(
                onDismissRequest = { grinderInfoShow = false },
                confirmButton = {
                    TextButton(onClick = { grinderInfoShow = false }) { Text("Close", color = com.coffeehmi.app.ui.theme.Gold) }
                },
                title = { Text("Grinder Diagnostics") },
                text = {
                    Text("• Grinder calibration: Active\n• Grind Size Setting: Level 3.4 (Fine)\n• Grinder Burr Temperature: 42°C\n• Current sensor check: OK")
                }
            )
        }

        // Temperature Info Dialog
        if (tempInfoShow) {
            AlertDialog(
                onDismissRequest = { tempInfoShow = false },
                confirmButton = {
                    TextButton(onClick = { tempInfoShow = false }) { Text("Close", color = com.coffeehmi.app.ui.theme.Gold) }
                },
                title = { Text("Boiler Diagnostics") },
                text = {
                    Text("• Boiler Heater state: Idle (Cycles: 840)\n• Steam Boiler Temp: 118°C\n• Brew Water Boiler Temp: 92.4°C\n• Pressure readings: 9.1 Bar (Stable)")
                }
            )
        }
    }
}

@Composable
private fun BeverageCard(
    beverage: Beverage,
    cardWidth: Dp,
    cardHeight: Dp,
    isBlocked: Boolean,
    isBinFull: Boolean,
    onClick: () -> Unit
) {
    val nameFontSize = (cardHeight.value * 0.11f).coerceIn(11f, 20f).sp
    val descFontSize = (cardHeight.value * 0.08f).coerceIn(8f, 13f).sp
    val textPad = (cardHeight.value * 0.07f).coerceIn(6f, 12f).dp

    Card(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .alpha(if (isBlocked) 0.6f else 1.0f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = beverage.imageRes),
                contentDescription = beverage.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                            startY = 180f
                        )
                    )
            )

            // Blocked Badges
            if (isBlocked) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            if (isBinFull) Color(0xFFD32F2F) else Color(0xFFF57C00),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isBinFull) "BIN FULL" else "REFILL",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Label
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(textPad)
            ) {
                Text(
                    text = beverage.name,
                    fontSize = nameFontSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = beverage.description,
                    fontSize = descFontSize,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
