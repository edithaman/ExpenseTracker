package com.example.expensetracker.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.expensetracker.data.model.ExpenseEntity
import com.example.expensetracker.presentation.viewmodel.ExpenseViewModel
import java.util.UUID

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddExpenseScreen(
    navController: NavController,
    viewModel: ExpenseViewModel
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    // ✅ Category dropdown setup
    val categoryList = listOf("Food", "Transport", "Shopping", "Bills", "Other")
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(categoryList.first()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Add Expense", style = MaterialTheme.typography.h6)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ✅ Category Dropdown UI
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categoryList.forEach { category ->
                    DropdownMenuItem(
                        onClick = {
                            selectedCategory = category
                            expanded = false
                        }
                    ) {
                        Text(text = category)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))



        Button(
            onClick = {
                val expense = ExpenseEntity(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    category = selectedCategory, // ✅ selected from dropdown
                    note = note,
                    date = System.currentTimeMillis()
                )


                if (amount.isBlank()) {
                    Toast.makeText(context, "Amount and Category required", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                viewModel.addExpense(expense)
                Toast.makeText(context, "Expense Added", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }
    }
}

