package com.example.dailybudget.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val date: String, // 支出日
    val amount: Double
)
