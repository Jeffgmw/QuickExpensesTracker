package com.example.quickexpensestracker.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.quickexpensestracker.repository.TransactionRepository
import com.example.quickexpensestracker.room.AppDatabase
import com.example.quickexpensestracker.model.Transaction
import kotlinx.coroutines.launch
import java.text.NumberFormat

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TransactionRepository(AppDatabase.getDatabase(application))

    var totalAmount = 0.0
    var budgetAmount = 0.0
    var expenseAmount = 0.0

    // Fetch all transactions, ordered by ascending or descending
    fun getAllTransactions(isAsc: Boolean): LiveData<List<Transaction>> {
        return repository.getAllTransactions(isAsc).asLiveData()
    }

    // Insert a new transaction
    fun insertTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.insertTransaction(transaction)
    }

    // Delete a transaction by ID
    fun deleteTransaction(transactionId: Int) = viewModelScope.launch {
        repository.deleteTransaction(transactionId)
    }

    // Update an existing transaction
    fun updateTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.updateTransaction(transaction)
    }

    // Fetch a transaction by ID
    fun getTransactionById(transactionId: Int): LiveData<Transaction> {
        return repository.getTransactionById(transactionId).asLiveData()
    }

    // Search transactions in the database
    fun searchDatabase(searchQuery: String, isAsc: Boolean): LiveData<List<Transaction>> {
        return repository.searchTransactions(searchQuery, isAsc).asLiveData()
    }

    // Update the dashboard values
    fun updateDashboard(transactions: List<Transaction>) {
        totalAmount = transactions.sumOf { it.amount }
        budgetAmount = transactions.filter { it.amount > 0 }.sumOf { it.amount }
        expenseAmount = totalAmount - budgetAmount
    }

    // Format the amount as currency
    fun formattedAmount(amount: Double): String {
        return NumberFormat.getCurrencyInstance().format(amount)
    }

    // Factory for creating instances of TransactionViewModel
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TransactionViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
