package com.example.dailybudget

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dailybudget.R
import java.text.SimpleDateFormat
import java.util.*

class BudgetSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_setting)

        // 各UIコンポーネントを取得
        val budgetInput = findViewById<EditText>(R.id.budgetInput)
        val salaryDateInput = findViewById<TextView>(R.id.salaryDateInput)
        val paymentOptionButton = findViewById<Button>(R.id.paymentOptionButton)
        val saveButton = findViewById<Button>(R.id.saveButton)

        // 給料日選択ボタンをクリックしたときの動作
        salaryDateInput.setOnClickListener {
            showDatePickerDialog(salaryDateInput)
        }

        // 前払い/後払い選択ボタンをクリックしたときの動作
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

        // 保存ボタンをクリックしたときの動作
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
            Toast.makeText(this, "設定が保存されました", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // カレンダーダイアログを表示する関数
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

    // 給料日が土日祝かどうかを判定する関数
    private fun isSalaryDateHoliday(salaryDate: String): Boolean {
        val date = parseDate(salaryDate) ?: return false
        val calendar = Calendar.getInstance()
        calendar.time = date
        return isHoliday(calendar)
    }

    // 祝日と土日を判定する関数
    private fun isHoliday(calendar: Calendar): Boolean {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val isWeekend = dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY

        // 固定祝日を追加（簡易例）
        val holidays = listOf(
            "2024-01-01", // 元日
            "2024-02-11", // 建国記念の日
            "2024-02-23"  // 天皇誕生日
        )

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)

        return isWeekend || holidays.contains(formattedDate)
    }

    // 前払い・後払いの選択ダイアログを表示する関数
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

    // 入力された予算と給料日を保存する関数
    private fun saveBudgetAndSalaryDate(budget: String, salaryDate: String) {
        val sharedPreferences = getSharedPreferences("BudgetPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("budget", budget)
        editor.putString("salaryDate", salaryDate)
        editor.apply()
    }

    // 日付文字列を解析する関数
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
