package com.example.dailybudget.data.dao


import Spending
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SpendingDao {
    @Insert
    suspend fun insert(spending: Spending)

    @Query("SELECT * FROM spending WHERE date = :date LIMIT 1")
    suspend fun getSpendingForDate(date: String): Spending?
}
