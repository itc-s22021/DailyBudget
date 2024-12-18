package com.example.dailybudget.data

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("DailyBudgetPrefs", Context.MODE_PRIVATE)

    // 月の予算を保存
    fun saveBudget(budget: Int) {
        sharedPreferences.edit()
            .putInt("budget", budget)
            .apply()
    }

    // 月の予算を取得
    fun getBudget(): Int {
        return sharedPreferences.getInt("budget", 0)
    }

    // 給料日を保存
    fun saveSalaryDay(salaryDay: Int) {
        sharedPreferences.edit()
            .putInt("salaryDay", salaryDay)
            .apply()
    }

    // 給料日を取得
    fun getSalaryDay(): Int {
        return sharedPreferences.getInt("salaryDay", 0)
    }

    // 日ごとの予算データを保存
    fun saveDailyBudgetData(dailyBudgets: List<DailyBudget>) {
        val editor = sharedPreferences.edit()
        editor.putInt("totalDays", dailyBudgets.size) // データの件数を保存
        dailyBudgets.forEachIndexed { index, budget ->
            editor.putString("date_$index", budget.date)
            editor.putInt("budget_$index", budget.budget)
            editor.putInt("spent_$index", budget.spent)
        }
        editor.apply() // 保存確定
    }

    // 日ごとの予算データを取得
    fun getDailyBudgetData(): List<DailyBudget> {
        val totalDays = sharedPreferences.getInt("totalDays", 0)
        val dailyBudgetList = mutableListOf<DailyBudget>()

        for (i in 0 until totalDays) {
            val date = sharedPreferences.getString("date_$i", "") ?: ""
            val budget = sharedPreferences.getInt("budget_$i", 0)
            val spent = sharedPreferences.getInt("spent_$i", 0)
            dailyBudgetList.add(DailyBudget(date, budget, spent))
        }

        return dailyBudgetList
    }

    // データを削除（リセット用）
    fun clearAllData() {
        sharedPreferences.edit().clear().apply()
    }
}

// 日ごとの予算データを表すデータクラス
data class DailyBudget(
    val date: String,
    val budget: Int,
    val spent: Int
)
