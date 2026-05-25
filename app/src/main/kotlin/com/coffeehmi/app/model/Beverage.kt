package com.coffeehmi.app.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.coffeehmi.app.R

data class Beverage(
    val id: String,
    val name: String,
    val description: String,
    val baseColor: Color,
    @DrawableRes val imageRes: Int,
    val customizable: Boolean = true,
    val prepTimeSeconds: Int = 30,
    val quote: String
)

val beverageCatalog = listOf(
    Beverage("cap", "Cappuchino", "Frothy and aromatic", Color(0xFF8D6E63), R.drawable.img_cappuccino, quote = "Equal parts espresso, warm milk, and dense, velvety foam for a perfect aromatic balance."),
    Beverage("lat", "Café Latte", "Creamy and mild", Color(0xFFD7CCC8), R.drawable.img_latte, quote = "Silky steamed milk poured gently over rich espresso, topped with a micro-foam layer."),
    Beverage("esp", "Espresso", "Rich and bold", Color(0xFF3E2723), R.drawable.img_espresso, quote = "A pure, intense shot of dark, golden-crema espresso to awaken your senses."),
    Beverage("ame", "Americano", "Smooth and balanced", Color(0xFF5D4037), R.drawable.img_americano, quote = "A rich espresso diluted with hot water, capturing the depth of a drip coffee with premium espresso character."),
    Beverage("tea", "DipTea", "Steeped tea infusion", Color(0xFFC67A32), R.drawable.img_diptea, quote = "A warm, comforting tea bag infusion steeped to release clean, refreshing notes."),
    Beverage("milk", "Hot Milk", "Warm and frothy", Color(0xFFECEFF1), R.drawable.img_hot_milk, quote = "Pure steamed milk, smooth, warm, and highly comforting."),
    Beverage("hot", "Hot Water", "Just hot water", Color(0xFF0288D1), R.drawable.img_hot_water, customizable = false, quote = "Pure, filtered hot water heated to the optimal temperature for tea or custom infusions."),
    Beverage("steam", "Steam", "Manual steam control", Color(0xFF00BCD4), R.drawable.img_steam, customizable = false, quote = "Deliver powerful steam to froth milk or heat beverages manually.")
)

