package com.example.dailybudget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailybudget.data.entity.Expense

class DailyExpenseAdapter(
    private val expenses: List<Expense>
) : RecyclerView.Adapter<DailyExpenseAdapter.DailyExpenseViewHolder>() {

    class DailyExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.tvDate)
        private val amountTextView: TextView = itemView.findViewById(R.id.tvBudget)

        fun bind(expense: Expense) {
            dateTextView.text = expense.date
            amountTextView.text = String.format("¥%,d", expense.amount)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_expense, parent, false)
        return DailyExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyExpenseViewHolder, position: Int) {
        holder.bind(expenses[position])
    }

    override fun getItemCount(): Int {
        return expenses.size
    }
}
