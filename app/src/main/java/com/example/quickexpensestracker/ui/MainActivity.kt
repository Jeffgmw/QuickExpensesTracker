package com.example.quickexpensestracker.ui

import android.content.Intent // Used to start new activities.
import androidx.appcompat.app.AppCompatActivity // Base class for activities that use the modern Android features.
import android.os.Bundle // Used to pass data between activities.
import android.view.Menu // Used to create options menu in the activity.
import androidx.activity.viewModels // Kotlin extension to get ViewModel instance.
import androidx.appcompat.widget.SearchView // Search widget used in the app bar.
import androidx.lifecycle.asLiveData // Extension function to convert Flow to LiveData.
import androidx.lifecycle.lifecycleScope // Provides a CoroutineScope for managing coroutines tied to the lifecycle of an Activity or Fragment.
import androidx.recyclerview.widget.LinearLayoutManager // LayoutManager for RecyclerView to arrange items in a linear list.
import com.example.quickexpensestracker.adapters.TransactionAdapter // Custom adapter for displaying transaction items in a RecyclerView.
import com.example.quickexpensestracker.data.SettingsDataStore // Class for managing application settings using DataStore.
import com.example.quickexpensestracker.model.Transaction // Data model class for transactions.
import com.example.quickexpensestracker.viewmodels.TransactionViewModel // ViewModel class for managing UI-related data in a lifecycle-conscious way.
import com.example.quickexpensestracker.viewmodels.TransactionViewModelFactory // Factory class for creating instances of TransactionViewModel.
import com.example.quickexpensetracker.R // Resource class for accessing application resources (e.g., strings, layouts, etc.).
import com.example.quickexpensetracker.databinding.ActivityMainBinding // Binding class for activity_main layout, allows direct access to UI components.
import kotlinx.coroutines.launch // Function to launch a new coroutine.

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var settingsDataStore: SettingsDataStore
    private var isAsc: Boolean = false
    private var searchView: SearchView? = null

    private val vm: TransactionViewModel by viewModels {
        TransactionViewModelFactory(application)
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeSettings()
        observeTransactions()
        setupListeners()
    }

    private fun setupViews() {
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        transactionAdapter = TransactionAdapter { transaction ->
            startDetailedActivity(transaction.id)
        }
        binding.recyclerview.adapter = transactionAdapter
    }


    private fun observeSettings() {
        settingsDataStore = SettingsDataStore(this)
        settingsDataStore.preferenceFlow.asLiveData().observe(this) { isAsc ->
            this.isAsc = isAsc
            refreshTransactionList()
        }
    }


    private fun observeTransactions() {
        vm.getAllTransactions(isAsc).observe(this) { transactions ->
            vm.updateDashboard(transactions)
            updateUI(transactions)
        }
    }


    private fun setupListeners() {
        binding.addBtn.setOnClickListener {
            startAddTransactionActivity()
        }

        binding.sortButton.setOnClickListener {
            toggleSortOrder()
        }
    }


    private fun startDetailedActivity(transactionId: Int) {
        val intent = Intent(this, DetailedActivity::class.java).apply {
            putExtra("transactionId", transactionId)
        }
        startActivity(intent)
    }


    private fun startAddTransactionActivity() {
        startActivity(Intent(this, AddTransactionActivity::class.java))
    }


    private fun toggleSortOrder() {
        lifecycleScope.launch {
            settingsDataStore.saveLayoutToPreferencesStore(!isAsc, this@MainActivity)
        }
    }


    private fun updateUI(transactions: List<Transaction>) {
        transactionAdapter.submitList(transactions)
        binding.balance.text = vm.formattedAmount(vm.totalAmount)
        binding.budget.text = vm.formattedAmount(vm.budgetAmount)
        binding.expense.text = vm.formattedAmount(vm.expenseAmount)
    }


    private fun refreshTransactionList() {
        val query = searchView?.query?.toString() ?: ""
        searchDatabase(query)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        val search = menu.findItem(R.id.menu_search)
        searchView = search?.actionView as? SearchView
        searchView?.apply {
            isSubmitButtonEnabled = true
            setOnQueryTextListener(this@MainActivity)
        }
        return true
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }


    override fun onQueryTextChange(query: String?): Boolean {
        query?.let { searchDatabase(it) }
        return true
    }

    private fun searchDatabase(query: String) {
        val searchQuery = "%$query%"
        vm.searchDatabase(searchQuery, isAsc).observe(this) { transactions ->
            updateUI(transactions)
        }
    }
}

