package com.coffeehmi.app.model

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object InventoryManager {
    // Ingredients
    private val _coffeeBeansGrams = MutableStateFlow(1000f) // Max 1000g
    val coffeeBeansGrams: StateFlow<Float> = _coffeeBeansGrams.asStateFlow()

    private val _milkMl = MutableStateFlow(2000f) // Max 2000ml
    val milkMl: StateFlow<Float> = _milkMl.asStateFlow()

    private val _waterMl = MutableStateFlow(5000f) // Max 5000ml
    val waterMl: StateFlow<Float> = _waterMl.asStateFlow()

    // Max capacities
    const val MAX_BEANS = 1000f
    const val MAX_MILK = 2000f
    const val MAX_WATER = 5000f

    // Ground Bin Counter (Robutness feature: block brew after 15 coffee brews until emptied)
    private val _groundBinCount = MutableStateFlow(0)
    val groundBinCount: StateFlow<Int> = _groundBinCount.asStateFlow()
    const val MAX_GROUND_BIN = 15

    // Maintenance PIN
    private val _maintenancePin = MutableStateFlow("1234")
    val maintenancePin: StateFlow<String> = _maintenancePin.asStateFlow()

    // System Diagnostics Logs
    private val _systemLogs = mutableStateListOf<String>()
    val systemLogs: List<String> = _systemLogs

    init {
        addLog("System boot. Diagnostics OK.")
    }

    fun addLog(msg: String) {
        val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        _systemLogs.add(0, "[$time] $msg")
        if (_systemLogs.size > 50) {
            _systemLogs.removeLast()
        }
    }

    // Dynamic Customizable Recipes
    data class BeverageRecipe(
        var coffeeBeans: Float, // slider value
        var brewWater: Float,  // slider value
        var hotMilk: Float,    // slider value
        var milkPriorityPre: Boolean // true for PRE, false for POST
    )

    private val recipes = mutableMapOf(
        "cap" to BeverageRecipe(0.9f, 10.5f, 7.8f, true),
        "lat" to BeverageRecipe(0.8f, 8.0f, 10.0f, false),
        "esp" to BeverageRecipe(1.2f, 3.0f, 0.0f, true),
        "ame" to BeverageRecipe(1.0f, 12.0f, 0.0f, true),
        "tea" to BeverageRecipe(0.0f, 15.0f, 0.0f, true),
        "milk" to BeverageRecipe(0.0f, 0.0f, 12.0f, true),
        "hot" to BeverageRecipe(0.0f, 15.0f, 0.0f, true),
        "steam" to BeverageRecipe(0.0f, 0.0f, 0.0f, true)
    )

    fun getRecipe(beverageId: String?): BeverageRecipe {
        return recipes[beverageId] ?: BeverageRecipe(0f, 0f, 0f, true)
    }

    fun saveRecipe(beverageId: String, coffeeBeans: Float, brewWater: Float, hotMilk: Float, milkPriorityPre: Boolean) {
        val r = recipes[beverageId] ?: BeverageRecipe(0f, 0f, 0f, true)
        r.coffeeBeans = coffeeBeans
        r.brewWater = brewWater
        r.hotMilk = hotMilk
        r.milkPriorityPre = milkPriorityPre
        recipes[beverageId] = r
        addLog("Recipe updated for: $beverageId")
    }

    // Convert slider settings into physical inventory consumption:
    // Beans: slider value * 12 grams
    // Water: slider value * 20 ml
    // Milk: slider value * 15 ml
    fun getRecipePhysicalRequirements(beverageId: String?): Triple<Float, Float, Float> {
        val r = getRecipe(beverageId)
        val beansReq = r.coffeeBeans * 12f
        val waterReq = r.brewWater * 20f
        val milkReq = r.hotMilk * 15f
        return Triple(beansReq, milkReq, waterReq)
    }

    fun canBrew(beverageId: String?): Boolean {
        if (_groundBinCount.value >= MAX_GROUND_BIN && isCoffeeBeverage(beverageId)) {
            return false
        }
        val (beans, milk, water) = getRecipePhysicalRequirements(beverageId)
        return _coffeeBeansGrams.value >= beans &&
               _milkMl.value >= milk &&
               _waterMl.value >= water
    }

    private fun isCoffeeBeverage(beverageId: String?): Boolean {
        return beverageId in listOf("cap", "lat", "esp", "ame")
    }

    fun brew(beverageId: String?) {
        val (beans, milk, water) = getRecipePhysicalRequirements(beverageId)
        _coffeeBeansGrams.value = (_coffeeBeansGrams.value - beans).coerceAtLeast(0f)
        _milkMl.value = (_milkMl.value - milk).coerceAtLeast(0f)
        _waterMl.value = (_waterMl.value - water).coerceAtLeast(0f)

        if (isCoffeeBeverage(beverageId)) {
            _groundBinCount.value = (_groundBinCount.value + 1).coerceAtMost(MAX_GROUND_BIN)
        }

        addLog("Brewed beverage: $beverageId (Used beans: ${beans.toInt()}g, milk: ${milk.toInt()}ml, water: ${water.toInt()}ml)")
    }

    fun refillAll() {
        _coffeeBeansGrams.value = MAX_BEANS
        _milkMl.value = MAX_MILK
        _waterMl.value = MAX_WATER
        addLog("All ingredient reservoirs refilled.")
    }

    fun emptyGroundBin() {
        _groundBinCount.value = 0
        addLog("Ground bin emptied.")
    }

    fun setMaintenancePin(newPin: String) {
        _maintenancePin.value = newPin
        addLog("Technician Access PIN updated.")
    }

    // Machine Model and Motor Config
    val machineModel = MutableStateFlow("Standard") // "Compact", "Standard", "Premium"
    val motorSpeeds = mutableMapOf<String, MutableStateFlow<Float>>()


    // ── Cleaning Settings State ───────────────────────────────────────────
    val milkCleanOnTime = MutableStateFlow(1.0f)
    val milkCleanOffTime = MutableStateFlow(1.0f)
    val milkCleanCycleCount = MutableStateFlow(1)
    val autoMilkCleanTime = MutableStateFlow(57)

    val brewerCleanOnTime = MutableStateFlow(9.4f)
    val brewerCleanCycle = MutableStateFlow(2)
    val autoBrewerCleanTime = MutableStateFlow(58)

    fun saveCleaningSettings(
        milkOn: Float, milkOff: Float, milkCycles: Int, milkAuto: Int,
        brewerOn: Float, brewerCycles: Int, brewerAuto: Int
    ) {
        milkCleanOnTime.value = milkOn
        milkCleanOffTime.value = milkOff
        milkCleanCycleCount.value = milkCycles
        autoMilkCleanTime.value = milkAuto
        brewerCleanOnTime.value = brewerOn
        brewerCleanCycle.value = brewerCycles
        autoBrewerCleanTime.value = brewerAuto
        addLog("Cleaning settings updated.")
    }
}
