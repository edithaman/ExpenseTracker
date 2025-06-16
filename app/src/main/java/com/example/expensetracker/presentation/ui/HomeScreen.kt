package com.example.expensetracker.presentation.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.expensetracker.R
import com.example.expensetracker.data.model.ExpenseEntity
import com.example.expensetracker.presentation.components.ExpensePieChart
import com.example.expensetracker.presentation.viewmodel.ExpenseViewModel
import com.example.expensetracker.utils.PDFGenerator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: ExpenseViewModel,
    onAddExpense: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val expenses by viewModel.expenses.collectAsState()
    var showFilter by remember { mutableStateOf(false) }
    var filteredExpenses by remember { mutableStateOf(expenses) }
    var showChart by remember { mutableStateOf(false) }
    var showExport by remember { mutableStateOf(false) }

    var showEditDialog by remember { mutableStateOf(false) }
    var selectedExpense by remember { mutableStateOf<ExpenseEntity?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var expenseToDelete by remember { mutableStateOf<ExpenseEntity?>(null) }





    LaunchedEffect(expenses) {
        filteredExpenses = expenses
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expense Tracker") },
                actions = {
                    IconButton(onClick = { showFilter = true }) {
                        Icon(Icons.Default.Search, contentDescription = "Filter")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 16.dp, end = 16.dp)
            ) {
                FloatingActionButton(onClick = { showChart = !showChart }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pie_chart),
                        contentDescription = "Pie Chart",
                        modifier = Modifier.size(24.dp)
                    )
                }
                FloatingActionButton(onClick = { showExport = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_file_download),
                        contentDescription = "Export PDF",
                        modifier = Modifier.size(24.dp)
                    )
                }
                FloatingActionButton(onClick = onAddExpense) {
                    Icon(Icons.Default.Add, contentDescription = "Add Expense")
                }
            }
        }

    ) { padding ->

        // List
        LazyColumn {
            items(filteredExpenses, key = { it.id }) { expense ->
                val dismissState = rememberDismissState(
                    confirmStateChange = { state ->
                        if (state == DismissValue.DismissedToEnd) {
                            selectedExpense = expense
                            showEditDialog = true
                            false
                        } else if (state == DismissValue.DismissedToStart) {
                            expenseToDelete = expense
                            showDeleteDialog = true
                            false // Prevent auto-dismiss
                        } else false
                    }

                )

                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(
                        DismissDirection.StartToEnd,
                        DismissDirection.EndToStart
                    ),
                    background = {
                        val color = when (dismissState.dismissDirection) {
                            DismissDirection.StartToEnd -> MaterialTheme.colors.primary
                            DismissDirection.EndToStart -> MaterialTheme.colors.surface
                            else -> MaterialTheme.colors.surface
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(20.dp)
                        ) {
                            val icon = when (dismissState.dismissDirection) {
                                DismissDirection.StartToEnd -> Icons.Default.Edit
                                DismissDirection.EndToStart -> Icons.Default.Delete
                                else -> null
                            }
                            icon?.let {
                                Icon(it, contentDescription = null, tint = Color.White)
                            }
                        }
                    },
                    dismissContent = {
                        ExpenseCard(expense)
                    }
                )
            }
        }
        if (showEditDialog && selectedExpense != null) {
            EditExpenseDialog(
                expense = selectedExpense!!,
                onDismiss = { showEditDialog = false },
                onSave = { updatedExpense ->
                    if (updatedExpense.amount.toString().isBlank() || updatedExpense.category.isBlank()) {
                        Toast.makeText(context, "Amount and Category required", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.updateExpense(updatedExpense)
                        showEditDialog = false
                    }
                }
            )
        }

        if (showDeleteDialog && expenseToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Expense") },
                text = { Text("Are you sure you want to delete this expense?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteExpense(expenses.toString())
                        showDeleteDialog = false
                        expenseToDelete = null
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        expenseToDelete = null
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }




        Column(modifier = Modifier
            .padding(padding)
            .padding(8.dp),
            Arrangement.Bottom) {


            // Chart
            //ExpenseBarChart(filteredExpenses)
            AnimatedVisibility(visible = showChart) {
                ExpensePieChart(expenses)
            }
            if (showExport) {
                if (filteredExpenses.isNotEmpty()) {
                    PDFGenerator.generatePDF(context, filteredExpenses)
                    Toast.makeText(context, "PDF exported", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "No data to export", Toast.LENGTH_SHORT).show()
                }
                showExport = false
            }

            // Export PDF
        }
    }

    if (showFilter) {
        FilterDialog(
            onApply = { category ->
                filteredExpenses = if (category.isNullOrBlank()) {
                    expenses
                } else {
                    expenses.filter {
                        it.category.contains(category.trim(), ignoreCase = true)
                    }
                }
                showFilter = false
            },
            onDismiss = { showFilter = false }
        )
    }
}

@Composable
fun ExpenseCard(expense: ExpenseEntity) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = expense.title, style = MaterialTheme.typography.h6)
            Text(text = "₹${expense.amount} • ${expense.category}")
            Text(
                text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(expense.date)),
                style = MaterialTheme.typography.caption
            )
        }
    }
}

@Composable
fun FilterDialog(
    onApply: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    var category by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter by Category") },
        text = {
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onApply(category) }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}






@Composable
fun EditExpenseDialog(
    expense: ExpenseEntity,
    onDismiss: () -> Unit,
    onSave: (ExpenseEntity) -> Unit
) {
    var title by remember { mutableStateOf(expense.title) }
    var amount by remember { mutableStateOf(expense.amount.toString()) }
    var category by remember { mutableStateOf(expense.category) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Expense") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    expense.copy(
                        title = title,
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        category = category
                    )
                )
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
