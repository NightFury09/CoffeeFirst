package com.coffeehmi.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun CompletionScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(5000)
        onTimeout()
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val h = maxHeight

        val iconSize       = (h.value * 0.28f).coerceIn(60f, 160f).dp
        val titleFontSize  = (h.value * 0.09f).coerceIn(20f, 52f).sp
        val subtitleFontSize = (h.value * 0.04f).coerceIn(12f, 22f).sp
        val bigSpacer      = (h.value * 0.06f).coerceIn(12f, 36f).dp
        val smallSpacer    = (h.value * 0.02f).coerceIn(4f, 12f).dp

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector     = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint            = MaterialTheme.colorScheme.primary,
                modifier        = Modifier.size(iconSize)
            )

            Spacer(modifier = Modifier.height(bigSpacer))

            Text(
                text       = "Enjoy your coffee!",
                fontWeight = FontWeight.SemiBold,
                fontSize   = titleFontSize,
                color      = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(smallSpacer))

            Text(
                text     = "Returning to menu in 5 seconds...",
                fontSize = subtitleFontSize,
                color    = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
