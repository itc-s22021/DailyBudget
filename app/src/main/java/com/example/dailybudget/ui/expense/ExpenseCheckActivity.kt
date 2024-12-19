package com.example.dailybudget.ui.expense

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_check)

        // RecyclerView の設定
        val recyclerView = findViewById<RecyclerView>(R.id.calendarView) // ID 修正
        recyclerView.layoutManager = LinearLayoutManager(this)

        // データ作成（1日毎の予算を計算）
        dailyBudgets = createDailyBudgetList().toMutableList()

        // Adapter をセット
        adapter = DailyBudgetAdapter(
            dailyBudgets,
            onItemClicked = { dailyBudget ->
                showManualInputDialog(dailyBudget)
            }
        )
        recyclerView.adapter = adapter

        // 音声入力ボタン
        findViewById<Button>(R.id.voiceInputButton).setOnClickListener {
            startVoiceInput()
        }
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
     * 手動入力ダイアログを表示する
     */
    private fun showManualInputDialog(dailyBudget: DailyBudget) {
        val editText = EditText(this)
        editText.hint = "支出額を入力してください"

        AlertDialog.Builder(this)
            .setTitle("${dailyBudget.date} の支出額")
            .setView(editText)
            .setPositiveButton("保存") { _, _ ->
                val input = editText.text.toString().toIntOrNull()
                if (input != null) {
                    dailyBudget.spent = input
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "無効な入力です", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("キャンセル", null)
            .show()
    }

    /**
     * 音声入力の結果を反映する
     */
    private fun updateDailyBudget(spentAmount: Int) {
        // 最新の支出日（例として最初の日を更新する）
        val todayBudget = dailyBudgets.firstOrNull()
        if (todayBudget != null) {
            todayBudget.spent = spentAmount
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "支出を更新しました", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "日付データがありません", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 日毎の予算リストを作成する
     */
    private fun createDailyBudgetList(): List<DailyBudget> {
        val sharedPreferences = getSharedPreferences("DailyBudgetPrefs", Context.MODE_PRIVATE)
        val totalBudget = sharedPreferences.getInt("budget", 30000)
        val salaryDay = sharedPreferences.getInt("salaryDay", 25)

        val calendar = Calendar.getInstance()
        val today = Calendar.getInstance()
        val currentDay = today.get(Calendar.DAY_OF_MONTH)

        if (currentDay > salaryDay) {
            calendar.add(Calendar.MONTH, 1)
        }
        calendar.set(Calendar.DAY_OF_MONTH, salaryDay)

        val daysUntilNextSalary = ((calendar.timeInMillis - today.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()

        if (daysUntilNextSalary <= 0) {
            Toast.makeText(this, "給料日設定が不正です", Toast.LENGTH_SHORT).show()
            return emptyList()
        }

        val dailyBudgetAmount = totalBudget / daysUntilNextSalary

        val dailyBudgets = mutableListOf<DailyBudget>()
        for (i in 0 until daysUntilNextSalary) {
            val currentDate = today.clone() as Calendar
            currentDate.add(Calendar.DAY_OF_YEAR, i)
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDate.time)
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
