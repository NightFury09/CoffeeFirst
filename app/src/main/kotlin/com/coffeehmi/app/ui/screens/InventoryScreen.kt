package com.coffeehmi.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffeehmi.app.model.InventoryManager
import com.coffeehmi.app.model.beverageCatalog
import com.coffeehmi.app.ui.theme.DeepEspresso
import com.coffeehmi.app.ui.theme.Gold
import com.coffeehmi.app.ui.theme.SuccessGreen

@Composable
fun InventoryScreen(onBack: () -> Unit) {
    val coffeeBeansGrams by InventoryManager.coffeeBeansGrams.collectAsState()
    val milkMl by InventoryManager.milkMl.collectAsState()
    val waterMl by InventoryManager.waterMl.collectAsState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepEspresso)
    ) {
        val screenW = maxWidth
        val screenH = maxHeight

        val topBarH = (screenH.value * 0.10f).coerceIn(40f, 64f).dp
        val contentH = screenH - topBarH

        val outerPad = (screenW.value * 0.03f).coerceIn(12f, 32f).dp
        val gap = (screenW.value * 0.02f).coerceIn(8f, 20f).dp

        val titleSize = (topBarH.value * 0.38f).coerceIn(14f, 24f).sp
        val labelSize = (screenH.value * 0.045f).coerceIn(12f, 20f).sp
        val bodySize = (screenH.value * 0.035f).coerceIn(10f, 15f).sp

        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topBarH)
                    .padding(horizontal = outerPad),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Gold
                    )
                }
                Text(
                    text = "Inventory & Dispense Statistics",
                    fontWeight = FontWeight.Bold,
                    fontSize = titleSize,
                    color = Gold
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { InventoryManager.refillAll() },
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = DeepEspresso),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refill", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Refill All", fontWeight = FontWeight.Bold, fontSize = bodySize)
                }
            }

            // Split screen content: Left (Tank Levels), Right (Dispense Stats / Est Cups)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(contentH)
                    .padding(outerPad),
                horizontalArrangement = Arrangement.spacedBy(gap)
            ) {
                // Left Side: Ingredient Taps
                Column(
                    modifier = Modifier
                        .weight(0.45f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x0DFFFFFF))
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Reservoir Status",
                        fontSize = labelSize,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )

                    // Beans Progress
                    IngredientLevelRow(
                        name = "Coffee Beans",
                        current = coffeeBeansGrams,
                        max = InventoryManager.MAX_BEANS,
                        unit = "g",
                        color = Color(0xFF8B5A2B),
                        bodySize = bodySize
                    )

                    // Milk Progress
                    IngredientLevelRow(
                        name = "Fresh Milk",
                        current = milkMl,
                        max = InventoryManager.MAX_MILK,
                        unit = "ml",
                        color = Color(0xFFE3DAC9),
                        bodySize = bodySize
                    )

                    // Water Progress
                    IngredientLevelRow(
                        name = "Water Tank",
                        current = waterMl,
                        max = InventoryManager.MAX_WATER,
                        unit = "ml",
                        color = Color(0xFF2196F3),
                        bodySize = bodySize
                    )
                }

                // Right Side: Dynamic Beverage Estimates
                Column(
                    modifier = Modifier
                        .weight(0.55f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x0DFFFFFF))
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Dispense Estimates (Approx. Remaining)",
                        fontSize = labelSize,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )

                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val gridW = maxWidth
                        val gridH = maxHeight
                        val rows = 4
                        val cols = 2
                        val itemGap = 8.dp
                        val itemH = (gridH - itemGap * (rows - 1)) / rows
                        
                        Column(verticalArrangement = Arrangement.spacedBy(itemGap)) {
                            for (r in 0 until rows) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().height(itemH),
                                    horizontalArrangement = Arrangement.spacedBy(itemGap)
                                ) {
                                    for (c in 0 until cols) {
                                        val idx = r * cols + c
                                        if (idx < beverageCatalog.size) {
                                            val beverage = beverageCatalog[idx]
                                            val (beansNeeded, milkNeeded, waterNeeded) = InventoryManager.getRecipePhysicalRequirements(beverage.id)

                                            var minEstimate = Int.MAX_VALUE

                                            if (beansNeeded > 0) {
                                                val est = (coffeeBeansGrams / beansNeeded).toInt()
                                                if (est < minEstimate) minEstimate = est
                                            }
                                            if (milkNeeded > 0) {
                                                val est = (milkMl / milkNeeded).toInt()
                                                if (est < minEstimate) minEstimate = est
                                            }
                                            if (waterNeeded > 0) {
                                                val est = (waterMl / waterNeeded).toInt()
                                                if (est < minEstimate) minEstimate = est
                                            }

                                            if (minEstimate == Int.MAX_VALUE) {
                                                minEstimate = 0
                                            }

                                            Box(modifier = Modifier.weight(1f)) {
                                                BeverageEstimateCard(
                                                    name = beverage.name,
                                                    cups = minEstimate,
                                                    baseColor = beverage.baseColor,
                                                    bodySize = bodySize
                                                )
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IngredientLevelRow(
    name: String,
    current: Float,
    max: Float,
    unit: String,
    color: Color,
    bodySize: androidx.compose.ui.unit.TextUnit
) {
    val pct = (current / max).coerceIn(0f, 1f)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = bodySize)
            Text(
                text = "${current.toInt()}/${max.toInt()} $unit (${(pct * 100).toInt()}%)",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = (bodySize.value * 0.9f).sp
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = pct,
            color = color,
            trackColor = Color(0x11FFFFFF),
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
        )
    }
}

@Composable
private fun BeverageEstimateCard(
    name: String,
    cups: Int,
    baseColor: Color,
    bodySize: androidx.compose.ui.unit.TextUnit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0x11FFFFFF), RoundedCornerShape(12.dp)),
        color = Color(0x08FFFFFF),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Small color dot
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(baseColor)
            )
            Column {
                Text(
                    text = name,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = bodySize
                )
                Text(
                    text = if (cups > 0) "$cups cup${if (cups > 1) "s" else ""} left" else "Out of stock",
                    color = if (cups > 0) SuccessGreen else Color(0xFFCF6679),
                    fontWeight = FontWeight.Medium,
                    fontSize = (bodySize.value * 0.9f).sp
                )
            }
        }
    }
}
