package com.example.dailybudget.ui.expense

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.dailybudget.R
import java.text.SimpleDateFormat
import java.util.*

class ExpenseCheckActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var todaySpending: Double = 0.0
    private var dailyBudget: Double = 0.0
    private var selectedDate: Calendar = Calendar.getInstance()  // 選択された日付を保持

    // UI コンポーネントの宣言
    private lateinit var todayDateTextView: TextView
    private lateinit var todaySpendingTextView: TextView
    private lateinit var todayBudgetTextView: TextView
    private lateinit var voiceInputButton: Button
    private lateinit var manualInputButton: Button

    // ActivityResultContracts のセットアップ
    private val speechRecognitionResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
                spokenText?.toDoubleOrNull()?.let {
                    updateSpending(it)
                } ?: Toast.makeText(this, "無効な金額が認識されました", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_check)

        // SharedPreferences の初期化
        sharedPreferences = getSharedPreferences("DailyBudgetPrefs", MODE_PRIVATE)

        // UI コンポーネントを取得
        todayDateTextView = findViewById(R.id.todayDateTextView)
        todaySpendingTextView = findViewById(R.id.todaySpendingTextView)
        todayBudgetTextView = findViewById(R.id.todayBudgetTextView)
        voiceInputButton = findViewById(R.id.voiceInputButton)
        manualInputButton = findViewById(R.id.manualInputButton)

        // インテントからデータを取得
        dailyBudget = intent.getDoubleExtra("dailyBudget", 0.0)
        todaySpending = getSpendingForSelectedDate()  // 選択された日付の支出額を取得
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
        updateSpendingTextColor()

        // 音声入力ボタンの動作
        voiceInputButton.setOnClickListener {
            startVoiceRecognition()
        }

        // 手動入力ボタンの動作
        manualInputButton.setOnClickListener {
            showManualInputDialog()
        }

        // 日付テキストビューをクリックした際に日付選択ダイアログを表示
        todayDateTextView.setOnClickListener {
            showDatePickerDialog()
        }
    }

    // 音声認識を開始
    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "支出額を話してください")
        speechRecognitionResultLauncher.launch(intent)
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

        // SharedPreferences に更新した支出額を保存
        saveSpendingForSelectedDate(todaySpending)

        // 更新された支出額と予算を表示
        todaySpendingTextView.text = "¥${"%.0f".format(todaySpending)}"
        todayBudgetTextView.text = "今日の予算: ¥${"%.0f".format(remainingBudget)}"

        // 支出額が予算を超えた場合、赤色に変更
        updateSpendingTextColor()
    }

    // 支出額の色を変更
    private fun updateSpendingTextColor() {
        if (todaySpending > dailyBudget) {
            todaySpendingTextView.setTextColor(resources.getColor(android.R.color.holo_red_dark))
        } else {
            todaySpendingTextView.setTextColor(resources.getColor(android.R.color.holo_green_dark))
        }
    }

    // 日付選択ダイアログを表示
    private fun showDatePickerDialog() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                selectedDate.set(year, month, dayOfMonth)
                updateDateDisplay()
                updateSpendingForSelectedDate()
            }

        // 現在の日付を選択肢として表示
        val datePickerDialog = DatePickerDialog(
            this,
            dateSetListener,
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    // 選択した日付を表示
    private fun updateDateDisplay() {
        val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
        todayDateTextView.text = dateFormat.format(selectedDate.time)
    }

    // 選択した日付に基づいて支出額を更新
    private fun updateSpendingForSelectedDate() {
        // 日付に関連する支出額の管理を行う処理を追加
        todaySpending = getSpendingForSelectedDate()
        todaySpendingTextView.text = "¥${"%.0f".format(todaySpending)}"
        val remainingBudget = dailyBudget - todaySpending
        todayBudgetTextView.text = "今日の予算: ¥${"%.0f".format(remainingBudget)}"
    }

    // 選択した日付の支出額を取得
    private fun getSpendingForSelectedDate(): Double {
        val dateKey = "spending_${selectedDate.timeInMillis}"
        return sharedPreferences.getFloat(dateKey, 0.0f).toDouble()
    }

    // 選択した日付の支出額を保存
    private fun saveSpendingForSelectedDate(amount: Double) {
        val dateKey = "spending_${selectedDate.timeInMillis}"
        with(sharedPreferences.edit()) {
            putFloat(dateKey, amount.toFloat())
            apply()
        }
    }
}
