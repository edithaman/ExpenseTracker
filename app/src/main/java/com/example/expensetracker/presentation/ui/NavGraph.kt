package com.example.expensetracker.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.expensetracker.presentation.viewmodel.AuthViewModel
import com.example.expensetracker.presentation.viewmodel.ExpenseViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val expenseViewModel: ExpenseViewModel = hiltViewModel()
    val user by authViewModel.user.collectAsState()


    NavHost(
        navController = navController,
        startDestination = if (user!= null) Screen.Home.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController, authViewModel)
        }

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = expenseViewModel,
                onAddExpense = { navController.navigate(Screen.AddExpense.route) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(navController, authViewModel)
        }

        composable(Screen.AddExpense.route) {
            AddExpenseScreen(navController = navController, viewModel = expenseViewModel)
        }

        composable(Screen.Camera.route) {
            CameraCaptureScreen(
                onTextExtracted = { text ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("ocrText", text)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}