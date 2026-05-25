package com.coffeehmi.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.coffeehmi.app.ui.screens.*

sealed class Screen(val route: String) {
    object Idle : Screen("idle")
    object BeverageSelect : Screen("beverage_select")
    object Customization : Screen("customization/{beverageId}") {
        fun createRoute(beverageId: String) = "customization/$beverageId"
    }
    object Brewing : Screen("brewing/{beverageId}") {
        fun createRoute(beverageId: String) = "brewing/$beverageId"
    }
    object Completion : Screen("completion")
    object Settings : Screen("settings")
    object PinEntry : Screen("pin_entry")
    
    // Maintenance Categories (Pic 5)
    object Maintenance : Screen("maintenance")
    object Technical : Screen("technical")
    object Operational : Screen("operational")
    object Factory : Screen("factory")

    // Maintenance Screens
    object RecipeSettings : Screen("recipe_settings")
    object CleaningSettings : Screen("cleaning_settings")
    object MotorSettings : Screen("motor_settings")
    object MotorMovement : Screen("motor_movement")
    object Inventory : Screen("inventory")
}

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Idle.route
    ) {
        composable(Screen.Idle.route) {
            IdleScreen(onStart = { navController.navigate(Screen.BeverageSelect.route) })
        }
        composable(Screen.BeverageSelect.route) {
            BeverageSelectScreen(
                onBeverageSelected = { id -> 
                    navController.navigate(Screen.Customization.createRoute(id))
                },
                onBack = { navController.popBackStack() },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.Customization.route) { backStackEntry ->
            val beverageId = backStackEntry.arguments?.getString("beverageId")
            CustomizationScreen(
                beverageId = beverageId,
                onConfirm = { id -> 
                    navController.navigate(Screen.Brewing.createRoute(id))
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Brewing.route) { backStackEntry ->
            val beverageId = backStackEntry.arguments?.getString("beverageId")
            BrewingScreen(
                beverageId = beverageId,
                onComplete = { navController.navigate(Screen.Completion.route) },
                onStop = { navController.popBackStack(Screen.BeverageSelect.route, false) }
            )
        }
        composable(Screen.Completion.route) {
            CompletionScreen(
                onTimeout = { 
                    navController.navigate(Screen.Idle.route) {
                        popUpTo(Screen.Idle.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToPinEntry = { navController.navigate(Screen.PinEntry.route) }
            )
        }
        composable(Screen.PinEntry.route) {
            PinEntryScreen(
                onCorrectPin = {
                    navController.navigate(Screen.Maintenance.route) {
                        popUpTo(Screen.Settings.route) // pop PIN screen on success
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }
        
        // ── Maintenance Main (Pic 5) ──
        composable(Screen.Maintenance.route) {
            MaintenanceScreen(
                onNavigateToOperational = { navController.navigate(Screen.Operational.route) },
                onNavigateToTechnical = { navController.navigate(Screen.Technical.route) },
                onNavigateToFactory = { navController.navigate(Screen.Factory.route) },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Sub-menus ──
        composable(Screen.Technical.route) {
            TechnicalSettingsScreen(
                onRecipeSettingClick = { navController.navigate(Screen.RecipeSettings.route) },
                onCleaningSettingsClick = { navController.navigate(Screen.CleaningSettings.route) },
                onBrewerMotorSettingsClick = { navController.navigate(Screen.MotorSettings.route) },
                onMotorMovementClick = { navController.navigate(Screen.MotorMovement.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Operational.route) {
            OperationalSettingsScreen(
                onNavigateToInventory = { navController.navigate(Screen.Inventory.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Factory.route) {
            FactorySettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ── Feature Screens ──
        composable(Screen.RecipeSettings.route) {
            RecipeSettingsScreen(
                onBack = { navController.popBackStack() },
                onTestBrew = { id -> 
                    navController.navigate(Screen.Brewing.createRoute(id))
                }
            )
        }
        composable(Screen.CleaningSettings.route) {
            CleaningSettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.MotorSettings.route) {
            MotorSettingsScreen(
                isMovementScreen = false,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.MotorMovement.route) {
            MotorSettingsScreen(
                isMovementScreen = true,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Inventory.route) {
            InventoryScreen(onBack = { navController.popBackStack() })
        }
    }
}
