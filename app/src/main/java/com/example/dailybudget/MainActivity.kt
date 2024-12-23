package com.example.dailybudget

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dailybudget.ui.expense.ExpenseCheckActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val budgetSettingButton: Button = findViewById(R.id.budgetSettingButton)
        val expenseCheckButton: Button = findViewById(R.id.expenseCheckButton)

        budgetSettingButton.setOnClickListener {
            startActivity(Intent(this, BudgetSettingActivity::class.java))
        }

        expenseCheckButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences("BudgetPreferences", MODE_PRIVATE)
            val budget = sharedPreferences.getString("budget", null)?.toDoubleOrNull()
            val salaryDate = sharedPreferences.getString("salaryDate", null)

            if (budget != null && salaryDate != null) {
                // 給料日までの日数を計算
                val dailyBudget = calculateDailyBudget(budget, salaryDate)

                // ExpenseCheckActivity にデータを渡す
                val intent = Intent(this, ExpenseCheckActivity::class.java)
                intent.putExtra("dailyBudget", dailyBudget)
                startActivity(intent)
            } else {
                // エラーを表示
                Toast.makeText(this, "まず予算を設定してください", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 1日あたりの分配金額を計算する関数
    private fun calculateDailyBudget(budget: Double, salaryDate: String): Double {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val salaryDateParsed = sdf.parse(salaryDate)
        val today = java.util.Calendar.getInstance().time

        val daysUntilSalary = ((salaryDateParsed.time - today.time) / (1000 * 60 * 60 * 24)).toInt() + 1
        return budget / daysUntilSalary
    }
}
