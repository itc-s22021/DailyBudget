package com.example.dailybudget.ui.expense

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailybudget.CalendarAdapter
import com.example.dailybudget.R
import java.util.Locale

class ExpenseCheckActivity : AppCompatActivity() {

    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var voiceInputButton: Button
    private lateinit var manualInputButton: Button
    private val REQUEST_CODE_SPEECH_INPUT = 1

    private lateinit var calendarAdapter: CalendarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_check)

        // Viewの初期化
        calendarRecyclerView = findViewById(R.id.calendarView)
        voiceInputButton = findViewById(R.id.voiceInputButton)
        manualInputButton = findViewById(R.id.manualInputButton)

        // カレンダーアダプターを設定
        setupCalendarView()

        // ボタンのクリックイベント
        voiceInputButton.setOnClickListener {
            startVoiceInput()
        }

        manualInputButton.setOnClickListener {
            openManualInputDialog()
        }
    }

    /**
     * カレンダービューの設定
     */
    private fun setupCalendarView() {
        calendarAdapter = CalendarAdapter(this, generateMockData())
        calendarRecyclerView.layoutManager = LinearLayoutManager(this)
        calendarRecyclerView.adapter = calendarAdapter
    }

    /**
     * 手動入力のダイアログを表示
     */
    private fun openManualInputDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_manual_input, null)
        val editTextAmount = dialogView.findViewById<EditText>(R.id.editTextAmount)

        AlertDialog.Builder(this)
            .setTitle("手動入力")
            .setView(dialogView)
            .setPositiveButton("登録") { _, _ ->
                val amount = editTextAmount.text.toString().toIntOrNull()
                if (amount != null) {
                    saveExpenseData(amount)
                } else {
                    Toast.makeText(this, "正しい金額を入力してください", Toast.LENGTH_SHORT).show()
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
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "金額を話してください")

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "音声入力をサポートしていません", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 音声認識の結果を取得
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = result?.get(0)
            val amount = spokenText?.replace("[^0-9]".toRegex(), "")?.toIntOrNull()

            if (amount != null) {
                saveExpenseData(amount)
            } else {
                Toast.makeText(this, "数値が認識できませんでした", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 支出データを保存
     */
    private fun saveExpenseData(amount: Int) {
        // ここにデータ保存の処理を記述 (SharedPreferencesやRoomなど)
        Toast.makeText(this, "支出額: ¥$amount を登録しました", Toast.LENGTH_SHORT).show()
        // カレンダーデータを更新する処理を追加する
    }

    /**
     * モックデータを生成 (仮のデータ)
     */
    private fun generateMockData(): List<String> {
        return List(30) { "日付: ${it + 1}日 - 予定額: ¥1000" }
    }
}
