package com.example.quickexpensestracker.viewmodels

import android.app.Application // Import Application class from android.app package.
import androidx.lifecycle.* // Import necessary classes from androidx.lifecycle package.
import com.example.quickexpensestracker.repository.TransactionRepository // Import TransactionRepository class from repository package.
import com.example.quickexpensestracker.room.AppDatabase // Import AppDatabase class from room package.
import com.example.quickexpensestracker.model.Transaction // Import Transaction class from model package.
import kotlinx.coroutines.launch // Import launch function from kotlinx.coroutines package.
import java.text.NumberFormat // Import NumberFormat class from java.text package.

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    // Create an instance of TransactionRepository using the application context.
    private val repository = TransactionRepository(AppDatabase.getDatabase(application))

    // Declare variables to hold dashboard values.
    var totalAmount = 0.0
    var budgetAmount = 0.0
    var expenseAmount = 0.0

    // Fetch all transactions from the repository and return as LiveData, ordered by ascending or descending.
    fun getAllTransactions(isAsc: Boolean): LiveData<List<Transaction>> {
        return repository.getAllTransactions(isAsc).asLiveData()
    }

    // Insert a new transaction into the repository.
    fun insertTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.insertTransaction(transaction)
    }

    // Delete a transaction by its ID from the repository.
    fun deleteTransaction(transactionId: Int) = viewModelScope.launch {
        repository.deleteTransaction(transactionId)
    }

    // Update an existing transaction in the repository.
    fun updateTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.updateTransaction(transaction)
    }

    // Fetch a transaction by its ID from the repository and return as LiveData.
    fun getTransactionById(transactionId: Int): LiveData<Transaction> {
        return repository.getTransactionById(transactionId).asLiveData()
    }

    // Search transactions in the repository based on the query string.
    fun searchDatabase(searchQuery: String, isAsc: Boolean): LiveData<List<Transaction>> {
        return repository.searchTransactions(searchQuery, isAsc).asLiveData()
    }

    // Update the dashboard values based on the list of transactions.
    fun updateDashboard(transactions: List<Transaction>) {
        totalAmount = transactions.sumOf { it.amount }
        budgetAmount = transactions.filter { it.amount > 0 }.sumOf { it.amount }
        expenseAmount = totalAmount - budgetAmount
    }

    // Format the amount as currency using NumberFormat.
    fun formattedAmount(amount: Double): String {
        return NumberFormat.getCurrencyInstance().format(amount)
    }

}
