package com.example.dailybudget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DailyBudgetAdapter(
    private val dailyBudgets: List<DailyBudget>,
    private val onItemClicked: (DailyBudget) -> Unit
) : RecyclerView.Adapter<DailyBudgetAdapter.DailyBudgetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyBudgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daily_budget, parent, false)
        return DailyBudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyBudgetViewHolder, position: Int) {
        val dailyBudget = dailyBudgets[position]
        holder.bind(dailyBudget)
    }

    override fun getItemCount(): Int = dailyBudgets.size

    inner class DailyBudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val budgetTextView: TextView = itemView.findViewById(R.id.budgetTextView)
        private val spentTextView: TextView = itemView.findViewById(R.id.spentTextView)
        private val differenceTextView: TextView = itemView.findViewById(R.id.differenceTextView)

        fun bind(dailyBudget: DailyBudget) {
            dateTextView.text = dailyBudget.date
            budgetTextView.text = "¥${dailyBudget.budget}"
            spentTextView.text = "¥${dailyBudget.spent}"
            differenceTextView.text = "差額: ¥${dailyBudget.getDifference()}"

            itemView.setOnClickListener {
                onItemClicked(dailyBudget)
            }
        }
    }
}
