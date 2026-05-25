package com.coffeehmi.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToPinEntry: () -> Unit
) {
    var idleTimeout by remember { mutableStateOf(60f) }

    // Full-screen BoxWithConstraints — no Scaffold, no invisible padding leaks
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val screenW = maxWidth
        val screenH = maxHeight

        val topBarH: Dp  = (screenH.value * 0.10f).coerceIn(40f, 64f).dp
        val contentH: Dp = screenH - topBarH

        val outerPad   = (screenW.value * 0.03f).coerceIn(12f, 32f).dp
        val itemGap    = (contentH.value * 0.05f).coerceIn(8f, 28f).dp
        val labelSize  = (contentH.value * 0.06f).coerceIn(12f, 24f).sp
        val bodySize   = (contentH.value * 0.04f).coerceIn(10f, 16f).sp
        val topLabelSz = (topBarH.value * 0.38f).coerceIn(14f, 24f).sp

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
                    text       = "System Settings",
                    fontWeight = FontWeight.Bold,
                    fontSize   = topLabelSz,
                    color      = MaterialTheme.colorScheme.onBackground
                )
            }

            // ── Content: exactly contentH tall ───────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(contentH)
                    .padding(outerPad),
                verticalArrangement = Arrangement.spacedBy(itemGap)
            ) {


                // Idle Timeout
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Idle Timeout: ${idleTimeout.toInt()} seconds",
                        fontSize   = labelSize,
                        fontWeight = FontWeight.SemiBold,
                        color      = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(itemGap * 0.3f))
                    Slider(
                        value         = idleTimeout,
                        onValueChange = { idleTimeout = it },
                        valueRange    = 30f..300f,
                        steps         = 8
                    )
                }

                Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f))

                // Maintenance Mode
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                        )
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                        .clickable(onClick = onNavigateToPinEntry)
                        .padding(outerPad * 0.6f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Maintenance Mode",
                            fontSize   = labelSize,
                            fontWeight = FontWeight.SemiBold,
                            color      = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Run diagnostics, calibrate components, and check stock levels.",
                            fontSize = bodySize,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}
