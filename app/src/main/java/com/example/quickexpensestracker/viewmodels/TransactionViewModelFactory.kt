package com.example.quickexpensestracker.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

class TransactionViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    // Override the create method to create instances of ViewModel with additional CreationExtras.
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        // Check if the requested ViewModel class is TransactionViewModel.
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Return a new instance of TransactionViewModel with the provided application context.
            return TransactionViewModel(application) as T
        }
        // Throw an IllegalArgumentException if the ViewModel class is unknown.
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}
