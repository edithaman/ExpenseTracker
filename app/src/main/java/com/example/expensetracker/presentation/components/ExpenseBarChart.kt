package com.example.expensetracker.presentation.components

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.expensetracker.data.model.ExpenseEntity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExpenseBarChart(expenses: List<ExpenseEntity>) {
    val grouped = expenses.groupBy {
        SimpleDateFormat("MMM", Locale.getDefault()).format(Date(it.date))
    }.mapValues { it.value.sumOf { e -> e.amount } }

    val entries = grouped.entries.mapIndexed { index, entry ->
        BarEntry(index.toFloat(), entry.value.toFloat())
    }
    val labels = grouped.keys.toList()

    AndroidView(factory = { context ->
        BarChart(context).apply {
            description.isEnabled = false
            axisRight.isEnabled = false
            legend.isEnabled = false
            animateY(1000)
        }
    }, update = { chart ->
        val dataSet = BarDataSet(entries, "Monthly Expenses")
        dataSet.color = Color.rgb(100, 181, 246)
        val data = BarData(dataSet)
        chart.data = data

        val xAxis = chart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)

        chart.invalidate()
    }, modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
        .padding(8.dp))
}

