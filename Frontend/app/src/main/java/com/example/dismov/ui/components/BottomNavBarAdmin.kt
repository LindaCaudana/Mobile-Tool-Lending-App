package com.example.dismov.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItemAdmin(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun BottomNavBarAdmin(
    navController: NavHostController,
    currentRoute: String
) {
    val items = listOf(
        BottomNavItemAdmin("inventory", Icons.Default.Inventory, "Inventario"),
        BottomNavItemAdmin("history", Icons.Default.History, "Historial"),
        BottomNavItemAdmin("profile", Icons.Default.Person, "Perfil")
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
