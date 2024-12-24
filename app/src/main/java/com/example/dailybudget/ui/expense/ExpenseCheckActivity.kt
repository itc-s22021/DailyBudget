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
    private var selectedDate: Calendar = Calendar.getInstance()

    private lateinit var todayDateTextView: TextView
    private lateinit var todaySpendingTextView: TextView
    private lateinit var todayBudgetTextView: TextView
    private lateinit var voiceInputButton: Button
    private lateinit var manualInputButton: Button

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

        sharedPreferences = getSharedPreferences("DailyBudgetPrefs", MODE_PRIVATE)

        todayDateTextView = findViewById(R.id.todayDateTextView)
        todaySpendingTextView = findViewById(R.id.todaySpendingTextView)
        todayBudgetTextView = findViewById(R.id.todayBudgetTextView)
        voiceInputButton = findViewById(R.id.voiceInputButton)
        manualInputButton = findViewById(R.id.manualInputButton)

        dailyBudget = intent.getDoubleExtra("dailyBudget", 0.0)
        loadSpendingForDate(selectedDate)

        updateDateDisplay()
        voiceInputButton.setOnClickListener { startVoiceRecognition() }
        manualInputButton.setOnClickListener { showManualInputDialog() }
        todayDateTextView.setOnClickListener { showDatePickerDialog() }
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "支出額を話してください")
        speechRecognitionResultLauncher.launch(intent)
    }

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

    private fun updateSpending(amount: Double) {
        todaySpending += amount
        saveSpendingForDate(selectedDate, todaySpending)
        updateRemainingBudget()
    }

    private fun updateRemainingBudget() {
        val remainingBudget = dailyBudget - todaySpending
        todayBudgetTextView.text = "今日の予算: ¥${"%.0f".format(remainingBudget)}"
        todaySpendingTextView.text = "¥${"%.0f".format(todaySpending)}"
        todaySpendingTextView.setTextColor(
            resources.getColor(
                if (todaySpending > dailyBudget) android.R.color.holo_red_dark else android.R.color.holo_green_dark
            )
        )
    }

    private fun loadSpendingForDate(date: Calendar) {
        todaySpending = sharedPreferences.getFloat("spending_${date.timeInMillis}", 0.0f).toDouble()
        updateRemainingBudget()
    }

    private fun saveSpendingForDate(date: Calendar, spending: Double) {
        with(sharedPreferences.edit()) {
            putFloat("spending_${date.timeInMillis}", spending.toFloat())
            apply()
        }
    }

    private fun showDatePickerDialog() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                selectedDate.set(year, month, dayOfMonth)
                loadSpendingForDate(selectedDate)
                updateDateDisplay()
            }

        DatePickerDialog(
            this,
            dateSetListener,
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateDisplay() {
        val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
        todayDateTextView.text = dateFormat.format(selectedDate.time)
    }
}
