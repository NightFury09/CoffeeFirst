package com.coffeehmi.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffeehmi.app.R
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun IdleScreen(onStart: () -> Unit) {
    var currentTime by remember { mutableStateOf(LocalTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalTime.now()
            delay(1000)
        }
    }

    val greeting = when (currentTime.hour) {
        in 5..11  -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        in 17..21 -> "Good Evening"
        else      -> "Good Night"
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onStart)
    ) {
        val w = maxWidth
        val h = maxHeight

        // Font sizes scale with the shorter axis so they always fit
        val clockFontSize    = (h.value * 0.22f).coerceIn(48f, 140f).sp
        val greetingFontSize = (h.value * 0.06f).coerceIn(16f, 40f).sp
        val hintFontSize     = (h.value * 0.028f).coerceIn(10f, 18f).sp
        val outerPad         = (w.value * 0.04f).coerceIn(16f, 48f).dp
        val midSpacer        = (h.value * 0.08f).coerceIn(16f, 80f).dp

        // Background Image
        Image(
            painter = painterResource(id = R.drawable.img_espresso),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(outerPad),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "Coffee First Logo",
                modifier = Modifier
                    .size((h.value * 0.18f).coerceIn(40f, 100f).dp)
                    .padding(bottom = 12.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text       = currentTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                color      = Color.White,
                fontWeight = FontWeight.ExtraLight,
                fontSize   = clockFontSize
            )

            Text(
                text       = greeting,
                color      = com.coffeehmi.app.ui.theme.Gold,
                fontWeight = FontWeight.Light,
                fontSize   = greetingFontSize,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(midSpacer))

            Text(
                text      = "TAP TO START YOUR COFFEE JOURNEY",
                color     = Color.White.copy(alpha = 0.6f),
                fontSize  = hintFontSize,
                letterSpacing = 2.sp
            )
        }
    }
}
