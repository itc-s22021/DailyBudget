package com.example.dailybudget.ui.expense

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailybudget.DailyBudget
import com.example.dailybudget.R
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ExpenseCheckActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_check)

        // RecyclerView の設定
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
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
        val currentDate = LocalDate.now()

        // 現在の日付から次の給料日までの日数を計算
        val nextSalaryDate = if (currentDate.dayOfMonth > salaryDay) {
            currentDate.plusMonths(1).withDayOfMonth(salaryDay)
        } else {
            currentDate.withDayOfMonth(salaryDay)
        }
        val daysUntilNextSalary = ChronoUnit.DAYS.between(currentDate, nextSalaryDate).toInt()

        // 1日あたりの予算を計算
        val dailyBudgetAmount = totalBudget / daysUntilNextSalary

        // リストを作成
        val dailyBudgets = mutableListOf<DailyBudget>()
        for (i in 0 until daysUntilNextSalary) {
            val date = currentDate.plusDays(i.toLong())
            dailyBudgets.add(
                DailyBudget(
                    date = date.toString(), // 日付を文字列として追加
                    budget = dailyBudgetAmount,
                    spent = 0 // 初期状態では支出は0
                )
            )
        }

        return dailyBudgets
    }
}
