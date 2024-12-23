package com.example.dailybudget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dailybudget.data.entity.Expense
import com.example.dailybudget.data.repository.ExpenseRepository
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val expenseRepository: ExpenseRepository = ExpenseRepository(application)

    // LiveDataでUIに表示するための支出データ
    private val _expenses = MutableLiveData<List<Expense>>()
    val expenses: LiveData<List<Expense>> get() = _expenses

    // 特定の日の支出額
    private val _selectedDayExpense = MutableLiveData<Expense>()
    val selectedDayExpense: LiveData<Expense> get() = _selectedDayExpense

    // 支出金額を追加する関数
    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.addExpense(expense)
        }
    }

    // 特定の日の支出を取得する関数
    fun getExpenseByDay(date: String) {
        viewModelScope.launch {
            val expense = expenseRepository.getExpenseByDay(date)
            _selectedDayExpense.postValue(expense)
        }
    }

    // 支出の全体一覧を取得する関数
    fun getAllExpenses() {
        viewModelScope.launch {
            val expenseList = expenseRepository.getAllExpenses()
            _expenses.postValue(expenseList)
        }
    }

    // 支出を更新する関数
    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.updateExpense(expense)
        }
    }

    // 支出を削除する関数
    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expense)
        }
    }
}
