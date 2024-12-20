package com.example.dailybudget

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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
            startActivity(Intent(this, ExpenseCheckActivity::class.java))
        }
    }
}
