package com.example.dailybudget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MonthlyCalendarAdapter(
    private val context: Context,
    private val calendarDates: List<Date>,
    private val dailyBudget: Map<Date, Double>,
    private val onDaySelected: (Int) -> Unit
) : RecyclerView.Adapter<MonthlyCalendarAdapter.ViewHolder>() {

    // 日付フォーマット
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // ViewHolderクラス
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvBudget: TextView = view.findViewById(R.id.tvBudget)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_calendar_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val date = calendarDates[position]
        val formattedDate = dateFormat.format(date)
        val budget = dailyBudget[date]?.toInt() ?: 0

        holder.tvDate.text = formattedDate
        holder.tvBudget.text = context.getString(R.string.daily_budget_text, budget)

        // アイテムのクリックリスナー
        holder.itemView.setOnClickListener {
            onDaySelected(position)
        }
    }

    override fun getItemCount(): Int = calendarDates.size
}