package com.example.dailybudget.data

import android.content.Context
import android.content.SharedPreferences

class BudgetPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("BudgetPrefs", Context.MODE_PRIVATE)

    // 予算を保存
    fun setBudget(budget: Double) {
        prefs.edit().putFloat("budget", budget.toFloat()).apply()
    }

    // 予算を取得
    fun getBudget(): Double {
        return prefs.getFloat("budget", 0f).toDouble()
    }

    // 給料日を保存
    fun setSalaryDate(date: String) {
        prefs.edit().putString("salaryDate", date).apply()
    }

    // 給料日を取得
    fun getSalaryDate(): String {
        return prefs.getString("salaryDate", "") ?: ""
    }
}
