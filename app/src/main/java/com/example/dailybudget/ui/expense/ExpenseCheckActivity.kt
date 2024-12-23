package com.example.dailybudget.ui.expense

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dailybudget.R
import java.text.SimpleDateFormat
import java.util.*

class ExpenseCheckActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var todaySpending: Double = 0.0
    private var dailyBudget: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_check)

        // SharedPreferences の初期化
        sharedPreferences = getSharedPreferences("DailyBudgetPrefs", MODE_PRIVATE)

        // UI コンポーネントを取得
        val todayDateTextView: TextView = findViewById(R.id.todayDateTextView)
        val todaySpendingTextView: TextView = findViewById(R.id.todaySpendingTextView)
        val todayBudgetTextView: TextView = findViewById(R.id.todayBudgetTextView)
        val reallocateButton: Button = findViewById(R.id.reallocateButton)
        val voiceInputButton: Button = findViewById(R.id.voiceInputButton)
        val manualInputButton: Button = findViewById(R.id.manualInputButton)

        // インテントからデータを取得
        dailyBudget = intent.getDoubleExtra("dailyBudget", 0.0)
        todaySpending = sharedPreferences.getFloat("todaySpending", 0.0f).toDouble() // 保存された支出額を取得
        val remainingBudget = dailyBudget - todaySpending // 今日の残り予算を計算

        // 今日の日付を表示
        val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
        val todayDate = dateFormat.format(Date())
        todayDateTextView.text = todayDate

        // 今日の予算を表示（残り予算を表示）
        todayBudgetTextView.text = "今日の予算: ¥${"%.0f".format(remainingBudget)}"

        // 今日の支出額を表示 (小数点以下を切り捨て)
        todaySpendingTextView.text = "¥${"%.0f".format(todaySpending)}"

        // 支出額が予算を超えた場合、赤色に変更
        if (todaySpending > dailyBudget) {
            todaySpendingTextView.setTextColor(resources.getColor(android.R.color.holo_red_dark))
        } else {
            todaySpendingTextView.setTextColor(resources.getColor(android.R.color.holo_green_dark))
        }

        // 音声入力ボタンの動作
        voiceInputButton.setOnClickListener {
            startVoiceRecognition()
        }

        // 手動入力ボタンの動作
        manualInputButton.setOnClickListener {
            showManualInputDialog()
        }

        // 再分配ボタンの動作
        reallocateButton.setOnClickListener {
            reallocateBudget()
        }
    }

    // 音声認識を開始
    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "支出額を話してください")
        startActivityForResult(intent, 1)
    }

    // 音声認識結果を受け取る
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val spokenText = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            if (spokenText != null) {
                val spokenAmount = spokenText.toDoubleOrNull()
                // 小数点を切り捨てて表示
                if (spokenAmount != null) {
                    updateSpending(spokenAmount)
                } else {
                    Toast.makeText(this, "無効な金額が認識されました", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 手動入力用ダイアログを表示
    private fun showManualInputDialog() {
        val editText = EditText(this)
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("支出額を入力")
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                val input = editText.text.toString()
                val amount = input.toDoubleOrNull()
                // 小数点を切り捨てて表示
                if (amount != null) {
                    updateSpending(amount)
                } else {
                    Toast.makeText(this, "無効な金額です", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("キャンセル", null)
            .create()

        dialog.show()
    }

    // 支出額を更新し、予算を再計算
    private fun updateSpending(amount: Double) {
        todaySpending += amount // 今日の支出額に追加
        val remainingBudget = dailyBudget - todaySpending // 残り予算を計算
        val editor = sharedPreferences.edit()
        editor.putFloat("todaySpending", todaySpending.toFloat()) // 今日の支出額を保存
        editor.apply()

        // 更新された支出額と予算を表示
        findViewById<TextView>(R.id.todaySpendingTextView).text = "¥${"%.0f".format(todaySpending)}"
        findViewById<TextView>(R.id.todayBudgetTextView).text = "今日の予算: ¥${"%.0f".format(remainingBudget)}"

        // 支出額が予算を超えた場合、赤色に変更
        if (todaySpending > dailyBudget) {
            findViewById<TextView>(R.id.todaySpendingTextView).setTextColor(resources.getColor(android.R.color.holo_red_dark))
        } else {
            findViewById<TextView>(R.id.todaySpendingTextView).setTextColor(resources.getColor(android.R.color.holo_green_dark))
        }
    }

    // 再分配ボタンの処理 (再分配のロジックを追加する場所)
    private fun reallocateBudget() {
        // 再分配のロジックをここに追加
        Toast.makeText(this, "再分配の処理を実行", Toast.LENGTH_SHORT).show()
    }
}
