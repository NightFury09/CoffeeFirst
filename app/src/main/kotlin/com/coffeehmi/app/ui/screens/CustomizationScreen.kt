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
import com.coffeehmi.app.ui.theme.TextPrimary

@Composable
fun CustomizationScreen(
    beverageId: String?,
    onConfirm: (String) -> Unit,
    onBack: () -> Unit
) {
    val beverage    = beverageCatalog.find { it.id == beverageId }
    var strength    by remember { mutableStateOf(2f) }
    var temperature by remember { mutableStateOf(1) }
    var milkType    by remember { mutableStateOf(0) }

    // Full-screen BoxWithConstraints — no Scaffold, no invisible padding leaks
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val screenW = maxWidth
        val screenH = maxHeight

        // Top bar consumes a fixed fraction of screen height
        val topBarH: Dp  = (screenH.value * 0.10f).coerceIn(40f, 64f).dp
        // Everything else goes to the content area
        val contentH: Dp = screenH - topBarH

        // Derived sizes based on the true content area
        val outerPad   = (screenW.value * 0.018f).coerceIn(4f, 20f).dp
        val colGap     = (screenW.value * 0.020f).coerceIn(4f, 24f).dp
        val groupGap   = (contentH.value * 0.020f).coerceIn(4f, 18f).dp
        val segH: Dp   = (contentH.value * 0.10f).coerceIn(24f, 52f).dp
        val brewBtnH: Dp = (contentH.value * 0.13f).coerceIn(36f, 68f).dp
        val groupPad   = (screenW.value * 0.010f).coerceIn(4f, 14f).dp
        val labelSize  = (contentH.value * 0.035f).coerceIn(8f, 17f).sp
        val titleSize  = (contentH.value * 0.045f).coerceIn(10f, 21f).sp
        val brewSize   = (contentH.value * 0.050f).coerceIn(11f, 26f).sp
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
                    text       = beverage?.name ?: "Customize",
                    fontWeight = FontWeight.Bold,
                    fontSize   = topLabelSz,
                    color      = MaterialTheme.colorScheme.onBackground
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
                                painter            = painterResource(id = it.imageRes),
                                contentDescription = null,
                                modifier           = Modifier.fillMaxSize(),
                                contentScale       = ContentScale.Crop
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
                    Box(modifier = Modifier.weight(1f)) {
                        CustomizationGroup("Coffee Strength", groupPad, titleSize) {
                            Column {
                                Slider(
                                    value         = strength,
                                    onValueChange = { strength = it },
                                    valueRange    = 1f..3f,
                                    steps         = 1,
                                    modifier      = Modifier.height(32.dp), // Fixed height for slider area
                                    colors        = SliderDefaults.colors(
                                        thumbColor       = MaterialTheme.colorScheme.primary,
                                        activeTrackColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Mild",   fontSize = labelSize, color = TextPrimary)
                                    Text("Strong", fontSize = labelSize, color = TextPrimary)
                                }
                            }
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        CustomizationGroup("Temperature", groupPad, titleSize) {
                            SegmentedButtonSection(
                                selected  = temperature,
                                options   = listOf("Warm", "Hot", "Extra Hot"),
                                onSelect  = { temperature = it },
                                segH      = segH,
                                labelSize = labelSize
                            )
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        CustomizationGroup("Milk Selection", groupPad, titleSize) {
                            SegmentedButtonSection(
                                selected  = milkType,
                                options   = listOf("None", "Oat", "Whole", "Skim"),
                                onSelect  = { milkType = it },
                                segH      = segH,
                                labelSize = labelSize
                            )
                        }
                    }

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(brewBtnH),
                        onClick  = { beverageId?.let { onConfirm(it) } },
                        shape    = RoundedCornerShape(16.dp),
                        colors   = ButtonDefaults.buttonColors(
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
            .fillMaxSize() // Use fillMaxSize since it's inside a weight-Box
            .background(Color(0x0DFFFFFF), RoundedCornerShape(14.dp))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(14.dp))
            .padding(groupPad),
        verticalArrangement = Arrangement.Center // Center content vertically within the group
    ) {
        Text(
            title,
            fontSize   = titleSize,
            fontWeight = FontWeight.SemiBold,
            color      = MaterialTheme.colorScheme.primary
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
                color  = if (isSelected) MaterialTheme.colorScheme.primary else Color(0x1AFFFFFF),
                border = if (isSelected) null else BorderStroke(1.dp, Color(0x33FFFFFF))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        title,
                        fontSize   = labelSize,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color      = if (isSelected) MaterialTheme.colorScheme.onPrimary else TextPrimary
                    )
                }
            }
        }
    }
}
