package com.example.expensetracker.data.repository

import com.example.expensetracker.data.model.ExpenseEntity
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    val expenses: Flow<List<ExpenseEntity>>
    suspend fun addExpense(expense: ExpenseEntity)
    suspend fun deleteExpense(id: String)
    fun updateExpense(entity: com.example.expensetracker.data.model.ExpenseEntity)
}