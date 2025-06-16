package com.example.expensetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.ExpenseEntity
import com.example.expensetracker.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _filterCategory = MutableStateFlow<String?>(null)

    // 👇 Combine repository data + filter state
    val expenses: StateFlow<List<ExpenseEntity>> = combine(
        repository.expenses,
        _filterCategory
    ) { allExpenses, category ->
        if (category.isNullOrBlank()) {
            allExpenses
        } else {
            allExpenses.filter {
                it.category.contains(category.trim(), ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addExpense(expense: ExpenseEntity) = viewModelScope.launch {
        repository.addExpense(expense)
    }

    fun deleteExpense(id: String) = viewModelScope.launch {
        repository.deleteExpense(id)
    }

    fun updateExpense(entity: ExpenseEntity) {
        viewModelScope.launch {
            repository.updateExpense(entity)
        }
    }

    fun filterByCategory(category: String?) {
        _filterCategory.value = category
    }
}
