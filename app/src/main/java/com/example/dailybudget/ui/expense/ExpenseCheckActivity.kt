package com.example.dailybudget

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExpenseCheckActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_check)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        // ダミーデータのリスト
        val dailyBudgetList = listOf(
            DailyBudget("2024-01-01", 1500.0, 1200.0),
            DailyBudget("2024-01-02", 1500.0, 1400.0),
            DailyBudget("2024-01-03", 1500.0, 1700.0)
        )

        // アダプタを設定
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DailyBudgetAdapter(dailyBudgetList)
    }
}
