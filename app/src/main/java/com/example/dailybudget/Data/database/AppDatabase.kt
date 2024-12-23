package com.example.dailybudget.Data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dailybudget.data.dao.ExpenseDao
import com.example.dailybudget.data.entity.Expense

@Database(entities = [Expense::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}