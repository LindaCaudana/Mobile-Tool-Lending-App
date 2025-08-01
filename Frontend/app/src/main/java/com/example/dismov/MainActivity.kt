package com.example.dismov

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dismov.navigation.AppRoutes
import com.example.dismov.navigation.AdminNavigation
import com.example.dismov.navigation.UserNavigation
import com.example.dismov.ui.LoginScreen
import com.example.dismov.ui.admin.RegisterScreen
import com.example.dismov.ui.theme.DisMovTheme
import com.example.dismov.utils.TokenManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenManager.init(this)

        setContent {
            DisMovTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = AppRoutes.LOGIN
                ) {
                    composable(AppRoutes.LOGIN) {
                        LoginScreen(
                            onLoginSuccess = { role, token, userId ->
                                TokenManager.saveToken(token)
                                TokenManager.saveUserId(userId)

                                if (role == "admin") {
                                    navController.navigate(AppRoutes.ADMIN)
                                } else if (role == "user") {
                                    navController.navigate(AppRoutes.USER)
                                }
                            }
                        )
                    }

                    composable(AppRoutes.ADMIN) {
                        val token = TokenManager.getToken() ?: ""
                        AdminNavigation(
                            navController = navController,
                            token = token,
                            onLogout = {
                                TokenManager.clear()
                                navController.navigate(AppRoutes.LOGIN) {
                                    popUpTo(0)
                                }
                            }
                        )
                    }

                    composable(AppRoutes.USER) {
                        val token = TokenManager.getToken() ?: ""
                        val userId = TokenManager.getUserId() ?: ""

                        UserNavigation(
                            navController = navController,
                            token = token,
                            userId = userId,
                            onLogout = {
                                TokenManager.clear()
                                navController.navigate(AppRoutes.LOGIN) {
                                    popUpTo(0)
                                }
                            }
                        )
                    }

                    composable(AppRoutes.REGISTER) {
                        RegisterScreen(
                            onUserRegistered = {
                                navController.popBackStack() // Volver atrás al completar registro
                            },
                            onCancel = {
                                navController.popBackStack() // Volver atrás al presionar "Cancelar"
                            }
                        )
                    }

                }
            }
        }
    }
}
