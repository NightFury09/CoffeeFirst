package com.coffeehmi.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
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
import com.coffeehmi.app.ui.theme.DeepEspresso
import com.coffeehmi.app.ui.theme.Gold
import com.coffeehmi.app.ui.theme.ErrorRose
import kotlinx.coroutines.delay

@Composable
fun PinEntryScreen(
    onCorrectPin: () -> Unit,
    onCancel: () -> Unit
) {
    var pinInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val correctPin by com.coffeehmi.app.model.InventoryManager.maintenancePin.collectAsState()

    // Reset error when PIN input changes
    LaunchedEffect(pinInput) {
        if (isError && pinInput.isNotEmpty()) {
            isError = false
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepEspresso),
        contentAlignment = Alignment.Center
    ) {
        val w = maxWidth
        val h = maxHeight

        val outerPad = (w.value * 0.04f).coerceIn(12f, 32f).dp

        val titleSize = (h.value * 0.055f).coerceIn(14f, 30f).sp
        val subtitleSize = (h.value * 0.032f).coerceIn(9f, 16f).sp
        val errorSize = (h.value * 0.028f).coerceIn(9f, 14f).sp

        // Key size scales to ensure all 4 rows + header always fit
        val keySize = (h.value * 0.13f).coerceIn(44f, 76f).dp
        val keyGap = (h.value * 0.015f).coerceIn(6f, 14f).dp
        val keyTextSize = (keySize.value * 0.33f).coerceIn(14f, 26f).sp

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = outerPad, vertical = (h.value * 0.02f).coerceIn(8f, 20f).dp)
        ) {
            // Header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Technician Access",
                    fontWeight = FontWeight.Bold,
                    color = Gold,
                    fontSize = titleSize
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enter security PIN to access Maintenance Mode",
                    color = Gold.copy(alpha = 0.6f),
                    fontSize = subtitleSize
                )
            }

            // PIN Dots Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 4) {
                    val isFilled = i < pinInput.length
                    val dotColor = when {
                        isError -> ErrorRose
                        isFilled -> Gold
                        else -> Color(0x33FFFFFF)
                    }
                    Box(
                        modifier = Modifier
                            .size((keySize.value * 0.28f).coerceIn(14f, 24f).dp)
                            .clip(CircleShape)
                            .background(if (isFilled) dotColor else Color.Transparent)
                            .border(2.dp, dotColor, CircleShape)
                    )
                }
            }

            // Error display
            Box(modifier = Modifier.height(20.dp)) {
                if (isError) {
                    Text(
                        text = "Invalid PIN. Please try again.",
                        color = ErrorRose,
                        fontSize = errorSize,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Keypad Grid — all 4 rows always visible
            Column(
                verticalArrangement = Arrangement.spacedBy(keyGap),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val keypadRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("Cancel", "0", "Back")
                )

                keypadRows.forEach { rowKeys ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(keyGap * 1.2f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        rowKeys.forEach { key ->
                            KeypadButton(
                                text = key,
                                size = keySize,
                                textSize = keyTextSize,
                                onClick = {
                                    if (isError) {
                                        pinInput = ""
                                        isError = false
                                    }

                                    when (key) {
                                        "Cancel" -> onCancel()
                                        "Back" -> {
                                            if (pinInput.isNotEmpty()) {
                                                pinInput = pinInput.dropLast(1)
                                            }
                                        }
                                        else -> {
                                            if (pinInput.length < 4) {
                                                pinInput += key
                                                if (pinInput.length == 4) {
                                                    if (pinInput == correctPin) {
                                                        onCorrectPin()
                                                    } else {
                                                        isError = true
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    text: String,
    size: Dp,
    textSize: androidx.compose.ui.unit.TextUnit,
    onClick: () -> Unit
) {
    val isAction = text == "Cancel" || text == "Back"
    val backgroundColor = when {
        text == "Cancel" -> ErrorRose.copy(alpha = 0.15f)
        isAction -> Color(0x11FFFFFF)
        else -> Color(0x0DFFFFFF)
    }
    val contentColor = when {
        text == "Cancel" -> ErrorRose
        text == "Back" -> Gold
        else -> Color.White
    }
    val borderColor = if (isAction) contentColor.copy(alpha = 0.3f) else Color(0x1AFFFFFF)

    Surface(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .border(1.dp, borderColor, CircleShape),
        color = backgroundColor,
        shape = CircleShape
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (text == "Back") {
                Icon(
                    imageVector = Icons.Default.Backspace,
                    contentDescription = "Backspace",
                    tint = contentColor,
                    modifier = Modifier.size(size * 0.35f)
                )
            } else {
                Text(
                    text = text,
                    fontSize = if (text == "Cancel") textSize * 0.7f else textSize,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
        }
    }
}
