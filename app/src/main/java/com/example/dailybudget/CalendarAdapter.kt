package com.example.dailybudget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CalendarAdapter(
    private val dailyBudgetList: List<DailyBudget>,
    private val onItemClick: (DailyBudget) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(dailyBudget: DailyBudget) {
            // 各テキストビューを取得
            val dateTextView = itemView.findViewById<TextView>(R.id.dateTextView)
            val budgetTextView = itemView.findViewById<TextView>(R.id.budgetTextView)
            val spentTextView = itemView.findViewById<TextView>(R.id.spentTextView)
            val differenceTextView = itemView.findViewById<TextView>(R.id.differenceTextView)

            // 各ビューに値をセット
            dateTextView.text = dailyBudget.date
            budgetTextView.text = "予算: ¥${dailyBudget.budget}"
            spentTextView.text = "支出: ¥${dailyBudget.spent}"

            // 差額を計算
            val difference = dailyBudget.budget - dailyBudget.spent
            differenceTextView.text = "差額: ¥$difference"

            // 差額に応じた色を設定
            val color = if (difference >= 0) {
                ContextCompat.getColor(itemView.context, R.color.successGreen) // 差額がプラスの場合は緑
            } else {
                ContextCompat.getColor(itemView.context, R.color.errorRed) // 差額がマイナスの場合は赤
            }
            differenceTextView.setTextColor(color)

            // アイテムクリック時の動作を設定
            itemView.setOnClickListener {
                onItemClick(dailyBudget)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        // レイアウトをインフレートしてViewHolderを生成
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daily_budget, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        // 各アイテムをバインド
        holder.bind(dailyBudgetList[position])
    }

    override fun getItemCount(): Int = dailyBudgetList.size // アイテム数を返す
}
