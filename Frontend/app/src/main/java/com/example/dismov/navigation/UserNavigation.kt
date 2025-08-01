package com.example.dismov.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dismov.ui.components.BottomNavBarUser
import com.example.dismov.ui.user.AvailableToolsScreen
import com.example.dismov.ui.user.MyToolsScreen
import com.example.dismov.ui.user.UserProfileScreen
import com.example.dismov.viewmodel.LoanViewModel
import com.example.dismov.viewmodel.ToolViewModel

@Composable
fun UserNavigation(
    navController: NavHostController,
    token: String,
    userId: String,
    onLogout: () -> Unit
) {
    val userNavController = rememberNavController()
    val currentRoute = userNavController.currentBackStackEntryAsState().value?.destination?.route ?: "available"

    Scaffold(
        bottomBar = {
            BottomNavBarUser(navController = userNavController, currentRoute = currentRoute)
        }
    ) { padding ->
        NavHost(
            navController = userNavController,
            startDestination = "available",
            modifier = Modifier.padding(padding)
        ) {
            composable("available") {
                val toolViewModel: ToolViewModel = viewModel()
                AvailableToolsScreen(viewModel = toolViewModel, token = token)
            }
            composable("mytools") {
                val loanViewModel: LoanViewModel = viewModel()
                MyToolsScreen(token = token, userId = userId)
            }
            composable("profile") {
                UserProfileScreen(onLogout = onLogout)
            }
        }
    }
}
