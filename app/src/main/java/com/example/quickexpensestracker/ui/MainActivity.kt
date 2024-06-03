package com.example.quickexpensestracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickexpensestracker.adapters.TransactionAdapter
import com.example.quickexpensestracker.data.SettingsDataStore
import com.example.quickexpensestracker.model.Transaction
import com.example.quickexpensestracker.viewmodels.TransactionViewModel
import com.example.quickexpensestracker.viewmodels.TransactionViewModelFactory
import com.example.quickexpensetracker.R
import com.example.quickexpensetracker.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

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

        setSupportActionBar(binding.toolbar) // Set the toolbar as the action bar
        setupToolbar() // Setup toolbar menu actions
        setupViews() // Initialize views and RecyclerView
        observeSettings() // Observe settings data for sorting order
        observeTransactions() // Observe transaction data
        setupListeners() // Set up click listeners for buttons
    }


    private fun setupViews() {
        binding.recyclerview.layoutManager = LinearLayoutManager(this) // Set LinearLayoutManager to RecyclerView
        transactionAdapter = TransactionAdapter { transaction ->
            startDetailedActivity(transaction.id) // Start detailed activity on item click
        }
        binding.recyclerview.adapter = transactionAdapter // Set adapter to RecyclerView
    }


    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_search -> {
                    // Search action is handled in onCreateOptionsMenu
                    true
                }
                else -> false
            }
        }
    }


    private fun observeSettings() {
        settingsDataStore = SettingsDataStore(this) // Initialize SettingsDataStore
        settingsDataStore.preferenceFlow.asLiveData().observe(this) { isAsc ->
            this.isAsc = isAsc // Update sort order
            refreshTransactionList() // Refresh transaction list based on new sort order
        }
    }


    private fun observeTransactions() {
        vm.getAllTransactions(isAsc).observe(this) { transactions ->
            vm.updateDashboard(transactions) // Update dashboard data
            updateUI(transactions) // Update UI with new transaction data
        }
    }


    private fun setupListeners() {
        binding.addBtn.setOnClickListener {
            startAddTransactionActivity() // Start activity to add a new transaction
        }

        binding.sortButton.setOnClickListener {
            toggleSortOrder() // Toggle sort order
        }
    }


    private fun startDetailedActivity(transactionId: Int) {
        val intent = Intent(this, DetailedActivity::class.java).apply {
            putExtra("transactionId", transactionId) // Pass transaction ID to detailed activity
        }
        startActivity(intent) // Start detailed activity
    }


    private fun startAddTransactionActivity() {
        startActivity(Intent(this, AddTransactionActivity::class.java)) // Start add transaction activity
    }


    private fun toggleSortOrder() {
        lifecycleScope.launch {
            settingsDataStore.saveLayoutToPreferencesStore(!isAsc, this@MainActivity) // Save new sort order to preferences
        }
    }


    private fun updateUI(transactions: List<Transaction>) {
        transactionAdapter.submitList(transactions) // Update adapter with new transaction list
        binding.balance.text = vm.formattedAmount(vm.totalAmount) // Update balance text
        binding.budget.text = vm.formattedAmount(vm.budgetAmount) // Update budget text
        binding.expense.text = vm.formattedAmount(vm.expenseAmount) // Update expense text
    }


    private fun refreshTransactionList() {
        val query = searchView?.query?.toString() ?: "" // Get current query text
        searchDatabase(query) // Search database with query
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu) // Inflate menu with search item
        val search = menu.findItem(R.id.menu_search)
        searchView = search?.actionView as? SearchView
        searchView?.apply {
            isSubmitButtonEnabled = true // Enable submit button in search view
            setOnQueryTextListener(this@MainActivity) // Set query text listener
        }
        return true
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        return true // Handle query text submit (no action needed)
    }


    override fun onQueryTextChange(query: String?): Boolean {
        query?.let { searchDatabase(it) } // Search database as query text changes
        return true
    }


    private fun searchDatabase(query: String) {
        val searchQuery = "%$query%" // Prepare search query
        vm.searchDatabase(searchQuery, isAsc).observe(this) { transactions ->
            updateUI(transactions) // Update UI with search results
        }
    }
}
