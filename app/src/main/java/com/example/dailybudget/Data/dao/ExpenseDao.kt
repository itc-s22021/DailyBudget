package com.example.dailybudget.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.dailybudget.data.entity.Expense

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE date = :date")
    suspend fun getExpenseByDate(date: String): Expense?

    @Query("SELECT * FROM expenses")
    suspend fun getAllExpenses(): List<Expense>

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)
}
