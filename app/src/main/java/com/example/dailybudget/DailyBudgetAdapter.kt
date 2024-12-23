package com.example.dailybudget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DailyBudgetAdapter(private val dailyBudgetList: List<DailyBudget>) :
    RecyclerView.Adapter<DailyBudgetAdapter.DailyBudgetViewHolder>() {

    // ViewHolder の定義
    class DailyBudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val budgetTextView: TextView = itemView.findViewById(R.id.budgetTextView)
        val spentTextView: TextView = itemView.findViewById(R.id.spentTextView)
        val differenceTextView: TextView = itemView.findViewById(R.id.differenceTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyBudgetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_budget, parent, false)
        return DailyBudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyBudgetViewHolder, position: Int) {
        val dailyBudget = dailyBudgetList[position]
        holder.dateTextView.text = dailyBudget.date
        holder.budgetTextView.text = "¥%.0f".format(dailyBudget.budget)
        holder.spentTextView.text = "¥%.0f".format(dailyBudget.spent)
        holder.differenceTextView.text = "¥%.0f".format(dailyBudget.difference)
    }

    override fun getItemCount(): Int = dailyBudgetList.size
}
