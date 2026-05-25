package com.coffeehmi.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffeehmi.app.ui.theme.Gold
import com.coffeehmi.app.ui.theme.DeepEspresso

@Composable
fun MainMenuScreen(
    onNavigateToSelect: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepEspresso)
    ) {
        val w = maxWidth
        val h = maxHeight

        val titleFontSize    = (h.value * 0.09f).coerceIn(24f, 56f).sp
        val subtitleFontSize = (h.value * 0.045f).coerceIn(12f, 28f).sp
        val outerPad         = (w.value * 0.05f).coerceIn(16f, 48f).dp
        val btnSpacing       = (w.value * 0.03f).coerceIn(12f, 32f).dp
        val midSpacer        = (h.value * 0.10f).coerceIn(16f, 64f).dp

        // Buttons sized as a fraction of screen
        val btnW: Dp = (w.value * 0.22f).coerceIn(140f, 260f).dp
        val btnH: Dp = (h.value * 0.22f).coerceIn(60f, 130f).dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(outerPad),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text       = "Good Morning",
                fontWeight = FontWeight.Light,
                color      = Gold,
                fontSize   = titleFontSize
            )

            Text(
                text     = "What would you like to drink today?",
                color    = Gold.copy(alpha = 0.6f),
                fontSize = subtitleFontSize
            )

            Spacer(modifier = Modifier.height(midSpacer))

            Row(horizontalArrangement = Arrangement.spacedBy(btnSpacing)) {
                MenuButton(
                    text    = "Brew Now",
                    icon    = Icons.Default.PlayArrow,
                    onClick = onNavigateToSelect,
                    primary = true,
                    btnW    = btnW,
                    btnH    = btnH
                )

                MenuButton(
                    text    = "Settings",
                    icon    = Icons.Default.Settings,
                    onClick = onNavigateToSettings,
                    primary = false,
                    btnW    = btnW,
                    btnH    = btnH
                )
            }
        }
    }
}

@Composable
fun MenuButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    primary: Boolean,
    btnW: Dp,
    btnH: Dp
) {
    val iconSize    = (btnH.value * 0.30f).coerceIn(18f, 36f).dp
    val labelSize   = (btnH.value * 0.18f).coerceIn(12f, 24f).sp
    val iconGap     = (btnW.value * 0.05f).coerceIn(8f, 20f).dp

    Surface(
        modifier = Modifier
            .size(btnW, btnH)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .border(
                1.dp,
                if (primary) Gold else Color(0x33FFFFFF),
                RoundedCornerShape(24.dp)
            ),
        color = if (primary) Gold else Color(0x0DFFFFFF)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = iconGap),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = if (primary) DeepEspresso else Gold
            )
            Spacer(modifier = Modifier.width(iconGap))
            Text(
                text       = text,
                fontWeight = FontWeight.Bold,
                fontSize   = labelSize,
                color      = if (primary) DeepEspresso else Gold
            )
        }
    }
}
