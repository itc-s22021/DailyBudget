package com.example.dailybudget

data class DailyBudget(
    val date: String,       // 日付 (例: "2024-12-01")
    var budget: Int,        // 1日の予算金額
    var spent: Int          // その日に実際に使った金額
) {
    /**
     * 差額を計算します。
     * @return 差額 (予算 - 実際の支出)
     */
    fun getDifference(): Int {
        return budget - spent
    }

    /**
     * 差額がプラスかどうかを判定します。
     * @return 差額がプラスの場合はtrue、マイナスの場合はfalse
     */
    fun isUnderBudget(): Boolean {
        return getDifference() >= 0
    }
}
