package com.example.dailybudget.ui.expense

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.EditText
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

    private val REQUEST_CODE_SPEECH_INPUT = 100
    private lateinit var dailyBudgets: MutableList<DailyBudget>
    private lateinit var adapter: DailyBudgetAdapter
    private var selectedDailyBudget: DailyBudget? = null // 選択された日付を保持

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_check)

        // RecyclerView の設定
        val recyclerView = findViewById<RecyclerView>(R.id.calendarView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // データ作成（1日毎の予算を計算）
        dailyBudgets = createDailyBudgetList().toMutableList()

        // Adapter をセット
        adapter = DailyBudgetAdapter(
            dailyBudgets,
            onItemClicked = { dailyBudget ->
                selectedDailyBudget = dailyBudget
                showInputMethodDialog() // 入力方法選択ダイアログを表示
            }
        )
        recyclerView.adapter = adapter
    }

    /**
     * 入力方法選択ダイアログを表示する
     */
    private fun showInputMethodDialog() {
        val options = arrayOf("音声入力", "手動入力")
        AlertDialog.Builder(this)
            .setTitle("入力方法を選択してください")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> startVoiceInput() // 音声入力
                    1 -> showManualInputDialog() // 手動入力
                }
            }
            .setNegativeButton("キャンセル", null)
            .show()
    }

    /**
     * 手動入力ダイアログを表示する
     */
    private fun showManualInputDialog() {
        val editText = EditText(this)
        editText.hint = "支出額を入力してください"

        AlertDialog.Builder(this)
            .setTitle("手動入力")
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                val inputText = editText.text.toString().toIntOrNull()
                if (inputText != null) {
                    updateDailyBudget(inputText)
                } else {
                    Toast.makeText(this, "数値を入力してください", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("キャンセル", null)
            .show()
    }

    /**
     * 音声入力を開始する
     */
    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "支出額を話してください")
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(this, "音声入力が利用できません", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val inputText = result?.get(0)?.toIntOrNull()
            if (inputText != null) {
                updateDailyBudget(inputText)
            } else {
                Toast.makeText(this, "数値を認識できませんでした", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 入力結果を選択された日に反映する
     */
    private fun updateDailyBudget(spentAmount: Int) {
        selectedDailyBudget?.let { dailyBudget ->
            dailyBudget.spent = spentAmount
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "${dailyBudget.date} の支出を更新しました", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(this, "日付が選択されていません", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 日毎の予算リストを作成する
     */
    private fun createDailyBudgetList(): List<DailyBudget> {
        val sharedPreferences = getSharedPreferences("DailyBudgetPrefs", Context.MODE_PRIVATE)
        val totalBudget = sharedPreferences.getInt("budget", 30000)
        val salaryDay = sharedPreferences.getInt("salaryDay", 25)

        val today = Calendar.getInstance()
        val currentDay = today.get(Calendar.DAY_OF_MONTH)

        // 給料日の設定が正しいか確認
        if (salaryDay < 1 || salaryDay > today.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            Toast.makeText(this, "給料日の設定が不正です (1-${today.getActualMaximum(Calendar.DAY_OF_MONTH)})", Toast.LENGTH_LONG).show()
            return emptyList()
        }

        // 次の給料日を計算
        val nextSalaryDate = Calendar.getInstance()
        if (currentDay > salaryDay) {
            nextSalaryDate.add(Calendar.MONTH, 1)
        }
        nextSalaryDate.set(Calendar.DAY_OF_MONTH, salaryDay)

        // 日数を計算（小数切り上げを考慮）
        val daysUntilNextSalary = ((nextSalaryDate.timeInMillis - today.timeInMillis + (1000 * 60 * 60 * 24 - 1)) / (1000 * 60 * 60 * 24)).toInt()

        // 日数が0以下の場合はエラー
        if (daysUntilNextSalary <= 0) {
            Toast.makeText(this, "給料日設定が不正です", Toast.LENGTH_SHORT).show()
            return emptyList()
        }

        // 1日あたりの予算を計算
        val dailyBudgetAmount = totalBudget / daysUntilNextSalary

        // 日毎の予算リストを作成
        val dailyBudgets = mutableListOf<DailyBudget>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        for (i in 0 until daysUntilNextSalary) {
            val currentDate = today.clone() as Calendar
            currentDate.add(Calendar.DAY_OF_YEAR, i)
            val date = dateFormat.format(currentDate.time)
            dailyBudgets.add(
                DailyBudget(
                    date = date,
                    budget = dailyBudgetAmount,
                    spent = 0
                )
            )
        }

        return dailyBudgets
    }
}
