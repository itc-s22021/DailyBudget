package com.example.dailybudget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// DailyBudgetAdapterクラス
class DailyBudgetAdapter(
    private val dailyBudgets: List<DailyBudget> // 1日ごとの支出予定金額のリスト
) : RecyclerView.Adapter<DailyBudgetAdapter.DailyBudgetViewHolder>() {

    // ViewHolderの定義
    class DailyBudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView) // 日付
        val budgetTextView: TextView = itemView.findViewById(R.id.budgetTextView) // 支出予定金額
    }

    // onCreateViewHolder: ViewHolderを生成
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyBudgetViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_budget, parent, false) // アイテムレイアウトを膨らませる
        return DailyBudgetViewHolder(itemView)
    }

    // onBindViewHolder: ViewHolderにデータをバインド
    override fun onBindViewHolder(holder: DailyBudgetViewHolder, position: Int) {
        val dailyBudget = dailyBudgets[position] // 対象の支出データ

        // 日付と支出金額を設定
        holder.dateTextView.text = dailyBudget.date
        holder.budgetTextView.text = dailyBudget.budget.toString()
    }

    // アイテム数を返す
    override fun getItemCount(): Int {
        return dailyBudgets.size
    }
}
