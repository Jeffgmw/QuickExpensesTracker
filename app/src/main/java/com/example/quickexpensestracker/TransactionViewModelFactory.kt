package com.example.quickexpensestracker

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

class TransactionViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

//    @Suppress("DEPRECATION")
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        return create(modelClass, CreationExtras.Empty)
//    }
}
