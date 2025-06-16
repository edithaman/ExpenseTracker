package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.ExpenseDao
import com.example.expensetracker.data.model.ExpenseEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val dao: ExpenseDao,
    private val firestore: FirebaseFirestore
) : ExpenseRepository {

    override val expenses: Flow<List<ExpenseEntity>> = dao.getAllExpenses()

    override suspend fun addExpense(expense: ExpenseEntity) {
        dao.insertExpense(expense)
        firestore.collection("expenses").document(expense.id).set(expense)
    }

    override suspend fun deleteExpense(id: String) {
        dao.deleteExpense(id)
        firestore.collection("expenses").document(id).delete()
    }

    override fun updateExpense(entity: ExpenseEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.updateExpense(entity)
        }
    }

}