package com.coffeehmi.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.coffeehmi.app.model.Beverage

@Composable
fun BeverageIcon(beverage: Beverage, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = beverage.imageRes),
        contentDescription = beverage.name,
        modifier = modifier.clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}
