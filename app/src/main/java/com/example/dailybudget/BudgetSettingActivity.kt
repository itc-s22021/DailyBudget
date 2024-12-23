package com.example.dailybudget

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class BudgetSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_setting)

        val budgetInput = findViewById<EditText>(R.id.budgetInput)
        val salaryDateInput = findViewById<TextView>(R.id.salaryDateInput)
        val paymentOptionButton = findViewById<Button>(R.id.paymentOptionButton)
        val saveButton = findViewById<Button>(R.id.saveButton)

        val sharedPreferences = getSharedPreferences("BudgetPreferences", MODE_PRIVATE)

        val savedBudget = sharedPreferences.getString("budget", "")
        val savedSalaryDate = sharedPreferences.getString("salaryDate", "")

        if (!savedBudget.isNullOrEmpty()) {
            budgetInput.setText(savedBudget)
        }
        if (!savedSalaryDate.isNullOrEmpty()) {
            salaryDateInput.text = savedSalaryDate
        }

        salaryDateInput.setOnClickListener {
            showDatePickerDialog(salaryDateInput)
        }

        paymentOptionButton.setOnClickListener {
            val salaryDate = salaryDateInput.text.toString()
            if (salaryDate.isEmpty()) {
                Toast.makeText(this, "先に給料日を選択してください", Toast.LENGTH_SHORT).show()
            } else {
                val isHoliday = isSalaryDateHoliday(salaryDate)
                if (isHoliday) {
                    showPaymentOptionDialog()
                } else {
                    Toast.makeText(this, "給料日は平日です。選択は不要です", Toast.LENGTH_SHORT).show()
                }
            }
        }

        saveButton.setOnClickListener {
            val budget = budgetInput.text.toString()
            val salaryDate = salaryDateInput.text.toString()

            if (budget.isBlank()) {
                budgetInput.error = "予算を入力してください"
                return@setOnClickListener
            }
            if (salaryDate.isBlank()) {
                salaryDateInput.error = "給料日を選択してください"
                return@setOnClickListener
            }

            saveBudgetAndSalaryDate(budget, salaryDate)

            // 分配金額を計算して表示
            val dailyAmount = calculateDailyBudget(budget.toDouble(), salaryDate)
            if (dailyAmount != null) {
                Toast.makeText(this, "1日あたりの金額: ¥${"%.2f".format(dailyAmount)}", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "分配金額の計算に失敗しました", Toast.LENGTH_SHORT).show()
            }

            finish()
        }
    }

    private fun showDatePickerDialog(targetView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                targetView.text = formattedDate
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun isSalaryDateHoliday(salaryDate: String): Boolean {
        val date = parseDate(salaryDate) ?: return false
        val calendar = Calendar.getInstance()
        calendar.time = date
        return isHoliday(calendar)
    }

    private fun isHoliday(calendar: Calendar): Boolean {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val isWeekend = dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY

        val holidays = listOf(
            "2024-01-01",
            "2024-02-11",
            "2024-02-23"
        )

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)

        return isWeekend || holidays.contains(formattedDate)
    }

    private fun showPaymentOptionDialog() {
        val options = arrayOf("前払い", "後払い")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("給料日が土日祝の場合")
        builder.setItems(options) { _, which ->
            val selectedOption = options[which]
            Toast.makeText(this, "$selectedOption が選択されました", Toast.LENGTH_SHORT).show()
        }
        builder.setCancelable(true)
        builder.show()
    }

    private fun saveBudgetAndSalaryDate(budget: String, salaryDate: String) {
        val sharedPreferences = getSharedPreferences("BudgetPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("budget", budget)
        editor.putString("salaryDate", salaryDate)
        editor.apply()
    }

    private fun calculateDailyBudget(budget: Double, salaryDate: String): Double? {
        val currentDate = Calendar.getInstance().time
        val nextSalaryDate = parseDate(salaryDate) ?: return null

        if (nextSalaryDate.before(currentDate)) {
            return null // 給料日が過去の場合、計算しない
        }

        val differenceInMillis = nextSalaryDate.time - currentDate.time
        val daysUntilSalary = (differenceInMillis / (1000 * 60 * 60 * 24)).toInt()

        return if (daysUntilSalary > 0) {
            budget / daysUntilSalary
        } else {
            null
        }
    }

    private fun parseDate(dateString: String): Date? {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
