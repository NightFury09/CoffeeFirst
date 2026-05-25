package com.coffeehmi.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffeehmi.app.ui.theme.DeepEspresso
import com.coffeehmi.app.ui.theme.Gold
import com.coffeehmi.app.ui.theme.ErrorRose

@Composable
fun MaintenanceScreen(
    onNavigateToOperational: () -> Unit,
    onNavigateToTechnical: () -> Unit,
    onNavigateToFactory: () -> Unit,
    onBack: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepEspresso)
    ) {
        val screenW = maxWidth
        val screenH = maxHeight

        val topBarH = (screenH.value * 0.15f).coerceIn(48f, 72f).dp
        val contentH = screenH - topBarH

        val outerPad = (screenW.value * 0.05f).coerceIn(8f, 48f).dp
        val gap = (screenW.value * 0.04f).coerceIn(8f, 32f).dp

        val cardW = (screenW - outerPad * 2 - gap * 2) / 3
        val cardH = (contentH - outerPad * 1.5f).coerceAtLeast(100.dp) // Reduced minimum to ensure it fits small screens

        val titleSize = (topBarH.value * 0.35f).coerceIn(16f, 26f).sp
        val labelSize = (cardH.value * 0.10f).coerceIn(10f, 18f).sp

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topBarH)
                    .padding(horizontal = outerPad),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color(0xFF64B5F6),
                    modifier = Modifier.size(titleSize.value.dp * 1.2f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SETTINGS",
                    fontWeight = FontWeight.Bold,
                    fontSize = titleSize,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            }

            // Options Row (OPERATIONAL, TECHNICAL, FACTORY SETTINGS)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = outerPad),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(gap),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // OPERATIONAL
                    CategoryCard(
                        title = "OPERATIONAL",
                        icon = Icons.Default.Group,
                        iconColor = Color(0xFF64B5F6),
                        cardW = cardW,
                        cardH = cardH,
                        labelSize = labelSize,
                        onClick = onNavigateToOperational
                    )

                    // TECHNICAL
                    CategoryCard(
                        title = "TECHNICAL",
                        icon = Icons.Default.Build,
                        iconColor = Color(0xFFFF7043),
                        cardW = cardW,
                        cardH = cardH,
                        labelSize = labelSize,
                        onClick = onNavigateToTechnical
                    )

                    // FACTORY SETTINGS
                    CategoryCard(
                        title = "FACTORY SETTINGS",
                        icon = Icons.Default.Handyman,
                        iconColor = Color(0xFFFFD54F),
                        cardW = cardW,
                        cardH = cardH,
                        labelSize = labelSize,
                        onClick = onNavigateToFactory
                    )
                }
            }

            // Bottom row with red BACK button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = outerPad * 0.5f, end = outerPad),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRose),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 28.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "BACK",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    cardW: Dp,
    cardH: Dp,
    labelSize: androidx.compose.ui.unit.TextUnit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(cardW, cardH)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .border(2.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp)),
        color = Color(0x0DFFFFFF),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(cardH * 0.4f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x11FFFFFF))
                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(cardH * 0.22f)
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = title,
                fontSize = labelSize,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            )
        }
    }
}
