package com.example.dailybudget.ui.expense

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailybudget.DailyBudget
import com.example.dailybudget.DailyBudgetAdapter
import com.example.dailybudget.R
import java.text.SimpleDateFormat
import java.util.*

class ExpenseCheckActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_check)

        // RecyclerView の設定
        val recyclerView = findViewById<RecyclerView>(R.id.calendarView) // ID 修正
        recyclerView.layoutManager = LinearLayoutManager(this)

        // データ作成（1日毎の予算を計算）
        val dailyBudgets = createDailyBudgetList()

        // Adapter をセット
        val adapter = DailyBudgetAdapter(
            dailyBudgets,
            onItemClicked = { dailyBudget ->
                // クリック時の処理
                Toast.makeText(this, "Clicked: ${dailyBudget.date}", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = adapter
    }

    /**
     * 日毎の予算リストを作成する
     */
    private fun createDailyBudgetList(): List<DailyBudget> {
        // SharedPreferences から設定を取得
        val sharedPreferences = getSharedPreferences("DailyBudgetPrefs", Context.MODE_PRIVATE)
        val totalBudget = sharedPreferences.getInt("budget", 30000) // デフォルト: 30,000円
        val salaryDay = sharedPreferences.getInt("salaryDay", 25) // デフォルト: 25日

        // 現在の日付を Calendar を使って取得
        val calendar = Calendar.getInstance()
        val today = Calendar.getInstance() // 元の日付を保持
        val currentDay = today.get(Calendar.DAY_OF_MONTH)

        // 次の給料日までの日数を計算
        if (currentDay > salaryDay) {
            calendar.add(Calendar.MONTH, 1)
        }
        calendar.set(Calendar.DAY_OF_MONTH, salaryDay)

        val daysUntilNextSalary = ((calendar.timeInMillis - today.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()

        // 日数が0以下の場合のエラー回避
        if (daysUntilNextSalary <= 0) {
            Toast.makeText(this, "給料日設定が不正です", Toast.LENGTH_SHORT).show()
            return emptyList()
        }

        // 1日あたりの予算を計算
        val dailyBudgetAmount = totalBudget / daysUntilNextSalary

        // リストを作成
        val dailyBudgets = mutableListOf<DailyBudget>()
        for (i in 0 until daysUntilNextSalary) {
            val currentDate = today.clone() as Calendar
            currentDate.add(Calendar.DAY_OF_YEAR, i) // i日後に進める
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDate.time)
            dailyBudgets.add(
                DailyBudget(
                    date = date,
                    budget = dailyBudgetAmount,
                    spent = 0 // 初期状態では支出は0
                )
            )
        }

        return dailyBudgets
    }
}
