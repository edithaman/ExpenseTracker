package com.example.expensetracker.presentation.ui

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object AddExpense : Screen("add_expense")
    object Camera : Screen("camera")
    object SignUp : Screen("signup")
}