package com.example.expensetracker.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.ExpenseEntity

@Composable
fun ExpensePieChart(
    expenses: List<ExpenseEntity>,
    modifier: Modifier = Modifier
) {
    val categoryData = expenses
        .groupBy { it.category }
        .map { (category, list) -> category to list.sumOf { it.amount } }
        .filter { it.second > 0 }

    if (categoryData.isEmpty()) {
        Text("No expenses to display", modifier = modifier.padding(16.dp))
        return
    }

    val total = categoryData.sumOf { it.second }
    val sweepAngles = categoryData.map { ((it.second / total) * 360f).toFloat() }
    val categories = categoryData.map { it.first }

    val colorPalette = listOf(
        Color(0xFFEF5350), // Food - Red
        Color(0xFF42A5F5), // Transport - Blue
        Color(0xFF66BB6A), // Shopping - Green
        Color(0xFFFFA726), // Bills - Orange
        Color(0xFFAB47BC)  // Other - Purple
    )

    val colors = colorPalette.take(categories.size)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
       /* Canvas(modifier = Modifier.size(250.dp)) {
            var startAngle = -90f
            sweepAngles.forEachIndexed { index, sweep ->
                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true
                )
                startAngle += sweep
            }
        }*/
        Canvas(modifier = Modifier.size(250.dp)) {
            var startAngle = -90f
            sweepAngles.forEachIndexed { index, sweep ->
                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true
                )
                startAngle += sweep
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Legend
        Column {
            categories.forEachIndexed { index, category ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(colors[index], CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$category: ₹${categoryData[index].second.toInt()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

