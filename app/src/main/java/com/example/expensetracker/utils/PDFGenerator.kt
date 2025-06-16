package com.example.expensetracker.utils

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import com.example.expensetracker.data.model.ExpenseEntity
import java.io.File
import java.io.FileOutputStream

object PDFGenerator {
    fun generatePDF(context: Context, expenses: List<ExpenseEntity>) {
        val document = PdfDocument()
        val paint = Paint()
        val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        var y = 25
        paint.textSize = 10f

        for (expense in expenses.take(25)) {
            canvas.drawText("${expense.title} - ₹${expense.amount}", 10f, y.toFloat(), paint)
            y += 15
        }

        document.finishPage(page)

        val file = File(context.getExternalFilesDir(null), "Expense_Report.pdf")
        document.writeTo(FileOutputStream(file))
        document.close()

        Toast.makeText(context, "PDF saved: ${file.path}", Toast.LENGTH_LONG).show()
    }
}
