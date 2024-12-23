package com.example.dailybudget

// 1日ごとの予算データを管理するデータクラス
data class DailyBudget(
    val date: String,    // 日付 (例: "2024-01-01")
    val budget: Double,  // その日の予算金額
    val spent: Double    // 実際に使用した金額
) {
    // 予算と支出の差額を計算するプロパティ
    val difference: Double
        get() = budget - spent
}
