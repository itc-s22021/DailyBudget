package com.example.dailybudget.data.repository

import android.content.Context
import com.example.dailybudget.data.database.ExpenseDatabase
import com.example.dailybudget.data.entity.Expense

class ExpenseRepository(context: Context) {

    private val expenseDao = ExpenseDatabase.getInstance(context).expenseDao()

    suspend fun addExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    suspend fun getExpenseByDay(date: String): Expense? {
        return expenseDao.getExpenseByDate(date)
    }

    suspend fun getAllExpenses(): List<Expense> {
        return expenseDao.getAllExpenses()
    }

    suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense)
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }
}
