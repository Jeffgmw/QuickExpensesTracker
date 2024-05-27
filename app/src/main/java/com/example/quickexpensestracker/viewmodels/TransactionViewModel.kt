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

    // Factory for creating instances of TransactionViewModel.
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

/*
Explanation of source of methods and their functionality:

Application: This class is imported from the android.app package and represents the base class for maintaining global application state.

AndroidViewModel: This class is imported from the androidx.lifecycle package and is a subclass of ViewModel that provides the application context.

ViewModelScope: This class is imported from the androidx.lifecycle package and provides a scope for coroutines tied to the ViewModel lifecycle.

TransactionRepository: This class is imported from the repository package and acts as a mediator between different data sources (local and remote) and the ViewModel.

AppDatabase: This class is imported from the room package and represents the Room database.

Transaction: This class is imported from the model package and represents a transaction entity in the application.

NumberFormat: This class is imported from the java.text package and provides methods to format numbers as currency.
 */