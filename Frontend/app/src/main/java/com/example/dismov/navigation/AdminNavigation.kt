package com.example.dismov.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.dismov.ui.admin.InventoryScreen
import com.example.dismov.ui.admin.HistoryScreen
import com.example.dismov.ui.admin.ProfileScreen
import com.example.dismov.ui.components.BottomNavBarAdmin
import com.example.dismov.viewmodel.ToolViewModel
import com.example.dismov.viewmodel.LoanViewModel

@Composable
fun AdminNavigation(
    navController: NavHostController,
    token: String,
    onLogout: () -> Unit
) {
    val adminNavController = rememberNavController()
    val currentRoute = adminNavController.currentBackStackEntryAsState().value?.destination?.route ?: "inventory"

    Scaffold(
        bottomBar = {
            BottomNavBarAdmin(navController = adminNavController, currentRoute = currentRoute)
        }
    ) { padding ->
        NavHost(
            navController = adminNavController,
            startDestination = "inventory",
            modifier = Modifier.padding(padding)
        ) {
            composable("inventory") {
                val toolViewModel: ToolViewModel = viewModel()
                InventoryScreen(viewModel = toolViewModel, token = token)
            }
            composable("history") {
                val loanViewModel: LoanViewModel = viewModel()
                HistoryScreen(viewModel = loanViewModel, token = token)
            }
            composable("profile") {
                ProfileScreen(
                    onLogout = onLogout,
                    onRegisterUser = { navController.navigate("register") }
                )
            }
        }
    }
}
