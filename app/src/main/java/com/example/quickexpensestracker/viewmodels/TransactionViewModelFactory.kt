package com.example.quickexpensestracker.viewmodels

import android.app.Application // Import Application class from android.app package.
import androidx.lifecycle.ViewModel // Import ViewModel class from androidx.lifecycle package.
import androidx.lifecycle.ViewModelProvider // Import ViewModelProvider class from androidx.lifecycle package.
import androidx.lifecycle.viewmodel.CreationExtras // Import CreationExtras class from androidx.lifecycle.viewmodel package.

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
